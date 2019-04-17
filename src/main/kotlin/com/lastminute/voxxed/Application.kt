package com.lastminute.voxxed

import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.bigquery.*
import com.google.cloud.http.HttpTransportOptions
import java.io.File
import java.io.FileInputStream
import java.nio.channels.Channels
import java.nio.file.Files
import java.nio.file.Paths

fun main() {

    val datasetName = "auditing"
    val tableName = "APPLICATION_LOGS"
    val location = "EU"
    val projectId = "project identifier"

    val credentials = "read write key file".asCredentials()

    val tableConfigurations = tableConfigurations(datasetName, tableName)

    val bigQueryConnector = connectorFor(projectId, credentials)

    val writer = bigQueryConnector.writerFor(tableConfigurations, location)

    val stats = writer.loadData("logs.csv")

    println("Loaded ${stats.outputRows} records")

}

private fun tableConfigurations(
    datasetName: String,
    tableName: String
): WriteChannelConfiguration {
    val tableId = TableId.of(datasetName, tableName)
    return WriteChannelConfiguration.newBuilder(tableId)
        .setFormatOptions(csvOptions)
        .setAutodetect(true)
        .build()
}

private fun TableDataWriteChannel.loadData(data: String): JobStatistics.LoadStatistics {
    val csvPath = Paths.get("src/main/resources/$data")

    Channels.newOutputStream(this).use { stream -> Files.copy(csvPath, stream) }

    val job = this.job.waitFor()
    return job.getStatistics()
}

private val csvOptions = CsvOptions.newBuilder()
    .setFieldDelimiter(";")
    .setSkipLeadingRows(1)
    .build()

private fun connectorFor(
    projectId: String,
    credentials: ServiceAccountCredentials
): BigQuery = BigQueryOptions.newBuilder()
    .setProjectId(projectId)
    .setTransportOptions(HttpTransportOptions.newBuilder().build())
    .setCredentials(credentials).build().service


private fun String.asCredentials(): ServiceAccountCredentials {
    val credentialsPath = File("src/main/resources/$this")
    return ServiceAccountCredentials.fromStream(FileInputStream(credentialsPath))
}

private fun BigQuery.writerFor(
    tableConfigurations: WriteChannelConfiguration,
    location: String
): TableDataWriteChannel {
    val jobId = JobId.newBuilder().setLocation(location).build()
    return this.writer(jobId, tableConfigurations)
}
