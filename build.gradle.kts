plugins {
    java
    kotlin("jvm") version "1.3.61"
}

group = "io.github.abitgen"
version = "1.0-SNAPSHOT"

subprojects {
    repositories {
        mavenCentral()
        jcenter()
    }
}