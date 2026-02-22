output "ticket-receiver-url" {
  description = "Cloud Run ticket-receiver public URL"
  value = google_cloud_run_v2_service.cloud-run-ticket-receiver.uri
}

output "notification-server-url" {
  description = "Cloud Run notification-server public URL"
  value = google_cloud_run_v2_service.cloud-run-notification-server.uri
}