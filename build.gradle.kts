plugins {
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.allopen") version "1.6.21"
    id("io.quarkus")
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

dependencies {
    implementation("io.quarkus:quarkus-container-image-docker")
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
    implementation("io.quarkus:quarkus-resteasy-reactive-qute")
    implementation("io.quarkus:quarkus-rest-client-reactive-kotlin-serialization")
    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-redis-client")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-resteasy-reactive")
    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
}

group = "com.linux"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

allOpen {
    annotation("javax.ws.rs.Path")
    annotation("javax.enterprise.context.ApplicationScoped")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
    kotlinOptions.javaParameters = true
}

// TODO: De-duplicate following tasks with a single configurable task

task("herokuLogin") {
    group = "nokri"
    description="Login to heroku"
    doLast {
        exec {
            executable="heroku"
            args="container:login".split(" ")
            environment("HEROKU_API_KEY", System.getenv("HEROKU_API_KEY"))
        }
    }
}

task("herokuPushImage") {
    group = "nokri"
    description="Push Created docker Image to Heroku"
    dependsOn("herokuLogin", tasks.findByName("build"))
    doLast {
        exec {
            executable="docker"
            args="push registry.heroku.com/nokri-nokri/web".split(" ")
            environment("HEROKU_API_KEY", System.getenv("HEROKU_API_KEY"))
        }
    }
}

task("herokuDeploy") {
    group = "nokri"
    description="Trigger Deployment to heroku"
    dependsOn("herokuPushImage")
    doLast {
        exec {
            executable="heroku"
            args="container:release web --app nokri-nokri".split(" ")
            environment("HEROKU_API_KEY", System.getenv("HEROKU_API_KEY"))
        }
    }
}

task("herokuShowLogs") {
    group = "nokri"
    description="Show logs from heroku container"
    doLast {
        exec {
            executable="heroku"
            args="logs --app nokri-nokri --num 500 --force-colors".split(" ")
            environment("HEROKU_API_KEY", System.getenv("HEROKU_API_KEY"))
        }
    }
}