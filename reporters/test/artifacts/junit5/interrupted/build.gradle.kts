buildscript {
    val reporterJar: String by project
    dependencies { classpath(files(reporterJar)) }
}

plugins { java }

val reporterJar: String by project

repositories { mavenCentral() }

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation(platform("org.junit:junit-bom:5.11.0"))
    implementation("org.junit.jupiter:junit-jupiter")
    implementation("org.junit.platform:junit-platform-launcher")
    implementation(files(reporterJar))
}

tasks.register<JavaExec>("runInterrupted") {
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("InterruptedRunner")
}
