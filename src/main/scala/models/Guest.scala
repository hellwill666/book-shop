package com.example.models

import java.util.UUID

case class Guest(
  id: String = UUID.randomUUID().toString,
  name: String
)

object Guest {
  def create(name: String): Guest = Guest(name = name)
}
