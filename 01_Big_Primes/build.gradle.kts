plugins {
    java
    kotlin("jvm")
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