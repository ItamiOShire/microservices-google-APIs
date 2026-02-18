terraform {
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "7.7.0"
    }
  }
}
provider "google" {
  project = var.project-id
  region = var.region
}

# Pub/Sub

resource "google_pubsub_topic" "ticket_topic" {
  name = var.ticket-topic
}

resource "google_pubsub_subscription" "ticket_notification_sub" {
  name = var.notification-sub
  topic = google_pubsub_topic.ticket_topic.name
}

resource "google_pubsub_subscription" "ticket_process_sub" {
  name  = var.ticket-process-sub
  topic = google_pubsub_topic.ticket_topic.name
}

# Service account

resource "google_service_account" "helpdesk_sa" {
  account_id = var.sa-id
  display_name = "helpdesk-account"
}

# VM

resource "google_compute_instance" "vm-ticket-receiver" {
  name = "ticket-receiver-instance"
  machine_type = "e2-small"
  zone = var.zone

  boot_disk {
    initialize_params {
      image = "projects/cos-cloud/global/images/family/cos-stable"
    }
  }

  network_interface {
    network = "default"
  }

  metadata = {
    google-loggin-enable = "true"
    gce-container-declaration = <<-EOT
      spec:
        containers:
          - name: ticket-receiver-service
            image: "europe-central2-docker.pkg.dev/${var.project-id}/repo/${var.ticket-receiver-image-name}"
            env:
              -name: TICKET-TOPIC-NAME
              value: "${var.ticket-topic}"
            stdin: false
            tty: false
        restartPolicy: Always
    EOT

  }

  service_account {
    email = google_service_account.helpdesk_sa.email
    scopes = ["cloud-platform"]
  }

}

resource "google_compute_instance" "vm-notification-server" {
  name = "notification-server-instance"
  machine_type = "e2-small"
  zone = var.zone

  boot_disk {
    initialize_params {
      image = "projects/cos-cloud/global/images/family/cos-stable"
    }
  }

  network_interface {
    network = "default"
    access_config {}
  }

  metadata = {
    google-loggin-enable = "true"
    gce-container-declaration = <<-EOT
      spec:
        containers:
          - name: ticket-receiver-service
            image: "europe-central2-docker.pkg.dev/${var.project-id}/repo/${var.ticket-receiver-image-name}"
            env:
              -name: NOTIFICATION-SUB-NAME
              value: "${var.notification-sub}"
              -name: CLIENT-ID
              value: "${var.client-id}"
              -name: CLIENT-SECRET
              value: "${var.client-secret}"
              -name: REFRESH-TOKEN
              value: "${var.refresh-token}"
            stdin: false
            tty: false
        restartPolicy: Always
    EOT

  }

  service_account {
    email = google_service_account.helpdesk_sa.email
    scopes = ["cloud-platform"]
  }

}