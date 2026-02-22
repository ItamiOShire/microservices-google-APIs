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

# Cloud Run

resource "google_cloud_run_v2_service" "cloud-run-ticket-receiver" {
  name = "ticket-receiver-service"
  location = var.region
  deletion_protection = false
  ingress = "INGRESS_TRAFFIC_ALL"

  template {

    containers {
      name = "ticket-receiver-container"
      image = "europe-central2-docker.pkg.dev/${var.project-id}/microservices/${var.ticket-receiver-image-name}:latest"

      ports {
        container_port = 8080
      }

      env {
        name = "TICKET-TOPIC-NAME"
        value = var.ticket-topic
      }

    }

    service_account = google_service_account.helpdesk_sa.email

  }
}

resource "google_cloud_run_v2_service" "cloud-run-notification-server" {
  name = "notification-server-service"
  location = var.region
  deletion_protection = false
  ingress = "INGRESS_TRAFFIC_ALL"

  template {

    containers {
      name = "notification-server-container"
      image = "europe-central2-docker.pkg.dev/${var.project-id}/microservices/${var.notification-server-image-name}:latest"

      ports {
        container_port = 8080
      }

      env {
        name = "NOTIFICATION-SUB-NAME"
        value = var.notification-sub
      }

      env {
        name = "CLIENT-ID"
        value = var.client-id
      }

      env {
        name = "CLIENT-SECRET"
        value = var.client-secret
      }

      env {
        name = "REFRESH-TOKEN"
        value = var.refresh-token
      }

    }

    service_account = google_service_account.helpdesk_sa.email

  }
}

# IAM

resource "google_project_iam_member" "firestore-iam" {
  member = "serviceAccount:${google_service_account.helpdesk_sa.email}"
  project = var.project-id
  role = "roles/firebase.admin"
}

resource "google_project_iam_member" "pubsub-iam-sub" {
  member  = "serviceAccount:${google_service_account.helpdesk_sa.email}"
  project = var.project-id
  role    = "roles/pubsub.subscriber"
}

resource "google_project_iam_member" "pubsub-iam-pub" {
  member  = "serviceAccount:${google_service_account.helpdesk_sa.email}"
  project = var.project-id
  role    = "roles/pubsub.publisher"
}

resource "google_project_iam_member" "logging-iam" {
  member  = "serviceAccount:${google_service_account.helpdesk_sa.email}"
  project = var.project-id
  role    = "roles/logging.logWriter"
}

resource "google_project_iam_member" "artefact-registry" {
  member = "serviceAccount:${google_service_account.helpdesk_sa.email}"
  project = var.project-id
  role = "roles/artifactregistry.reader"
}

resource "google_cloud_run_v2_service_iam_member" "ticket-receiver" {
  member = "allUsers"
  project = var.project-id
  location = google_cloud_run_v2_service.cloud-run-ticket-receiver.location
  name   = google_cloud_run_v2_service.cloud-run-ticket-receiver.name
  role   = "roles/run.invoker"
}

resource "google_cloud_run_v2_service_iam_member" "notification-server" {
  member = "allUsers"
  project = var.project-id
  location = google_cloud_run_v2_service.cloud-run-notification-server.location
  name   = google_cloud_run_v2_service.cloud-run-notification-server.name
  role   = "roles/run.invoker"
}