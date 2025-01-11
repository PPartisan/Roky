resource "time_sleep" "sleep_wait" {
  depends_on = [docker_container.supabase-postgres]

  create_duration = "20s"
}

resource "null_resource" "db_setup_00" {
  provisioner "local-exec" {
    command = "docker exec -e PGPASSWORD=${random_password.POSTGRES_PASSWORD.result} -i ${var.POSTGRES_HOST} psql -h ${var.POSTGRES_HOST} -p ${var.POSTGRES_PORT} -U ${var.POSTGRES_USER} -d ${var.POSTGRES_DB} -f \"/home/init/config.sql\""
  }
  depends_on = [
    docker_container.supabase-postgres,
    time_sleep.sleep_wait
  ]
}
