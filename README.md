# Exponential growth with IaC superpowers

Talk performed by myself and [Francesco Gualazzi](https://github.com/inge4pres) at Voxxed Days Milano 2019

*Talk details*: [Exponential growth with IaC superpowers](https://vxdmilan2019.confinabox.com/talk/EOQ-0099  )

*Video*: [[VDM19] Crescita esponenziale dell'organizzazione con “Infrastructure as code” by Gualazzi & Corti](https://youtu.be/M9l4zclrhRI)

*Slides*: [SpeakerDeck](https://speakerdeck.com/araknoid/exponential-growth-with-iac-superpowers)

*Demo*: Code available in this repository

*Terraform script*:
```terraform
resource "google_bigquery_dataset" "auditing-dataset" {
  dataset_id = "auditing"
  location = "EU"
}

resource "google_bigquery_table" "APPLICATION_LOGS" {
  dataset_id = "${google_bigquery_dataset.auditing-dataset.dataset_id}"
  table_id = "APPLICATION_LOGS"

  time_partitioning {
    type = "DAY"
    expiration_ms = 1209600000
  }
}
``` 
