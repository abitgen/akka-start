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
    dependencies {
        apply(plugin = "org.jetbrains.kotlin.jvm")
        implementation(kotlin("stdlib-jdk8"))
        implementation(group = "com.typesafe.akka", name = "akka-actor-typed_${main.kotlin.Versions.ScalaBinary}", version = "2.6.6")
        //testCompile("junit", "junit", "4.12")
        testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
    }
}