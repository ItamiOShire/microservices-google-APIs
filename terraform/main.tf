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