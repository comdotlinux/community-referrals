import io.quarkus.runtime.configuration.ProfileManager.getActiveProfile

plugins {
    java
    id("io.quarkus")
    id("com.vaadin")
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project
val vaadinVersion: String by project
val vaadinQuarkusVersion: String by project

dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))

    implementation(enforcedPlatform("com.vaadin:vaadin-bom:$vaadinVersion"))
    // Vaadin
    implementation("com.vaadin:vaadin-core")
    implementation("com.vaadin:vaadin-core-jandex")
    implementation("com.vaadin:vaadin-quarkus:$vaadinQuarkusVersion")

    implementation("io.quarkus:quarkus-keycloak-authorization")
    implementation("io.quarkus:quarkus-security")
    implementation("io.quarkus:quarkus-oidc")
    implementation("io.quarkus:quarkus-websockets")
    implementation("io.quarkus:quarkus-container-image-docker")

    implementation("io.quarkus:quarkus-hibernate-validator")
    implementation("io.quarkus:quarkus-resteasy-jsonb")
    implementation("io.quarkus:quarkus-smallrye-openapi")
    implementation("io.quarkus:quarkus-hibernate-orm-panache")
    implementation("io.quarkus:quarkus-jdbc-postgresql")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-resteasy")

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
}

group = "com.linux"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
//    options.compilerArgs.add("-parameters")
}

vaadin {
    productionMode = "dev" != getActiveProfile()
}

// TODO: De-duplicate following tasks with a single configurable task

task("herokuLogin") {
    group = "referrals"
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
    group = "referrals"
    description="Push Created docker Image to Heroku"
    dependsOn("herokuLogin", tasks.findByName("build"))
    doLast {
        exec {
            executable="docker"
            args="push registry.heroku.com/community-referrals/web".split(" ")
            environment("HEROKU_API_KEY", System.getenv("HEROKU_API_KEY"))
        }
    }
}

task("herokuDeploy") {
    group = "referrals"
    description="Trigger Deployment to heroku"
    dependsOn("herokuPushImage")
    doLast {
        exec {
            executable="heroku"
            args="container:release web --app community-referrals".split(" ")
            environment("HEROKU_API_KEY", System.getenv("HEROKU_API_KEY"))
        }
    }
}

task("herokuShowLogs") {
    group = "referrals"
    description="Show logs from heroku container"
    doLast {
        exec {
            executable="heroku"
            args="logs --app community-referrals --num 500 --force-colors".split(" ")
            environment("HEROKU_API_KEY", System.getenv("HEROKU_API_KEY"))
        }
    }
}