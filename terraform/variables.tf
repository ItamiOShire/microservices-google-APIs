variable "project-id" {
  type = string
}
variable "region" {}
variable "zone" {}
variable "ticket-topic" {
  sensitive = true
}
variable "notification-sub" {}
variable "ticket-process-sub" {}
variable "sa-id" {
  sensitive = true
  ephemeral = true
}
variable "ticket-receiver-image-name" {}
variable "notification-server-image-name" {}
variable "client-id" {
  sensitive = true
  ephemeral = true
}
variable "client-secret" {
  sensitive = true
  ephemeral = true
}
variable "refresh-token" {
  sensitive = true
  ephemeral = true
}