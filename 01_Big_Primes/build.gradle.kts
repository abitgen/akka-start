plugins {
    java
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(group = "com.typesafe.akka", name = "akka-actor-typed_${main.kotlin.Versions.ScalaBinary}", version = "2.6.6")
    //testCompile("junit", "junit", "4.12")
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

tasks.test{
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}