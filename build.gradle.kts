plugins {
    kotlin("jvm") version "1.9.23"
    `maven-publish`
}

group = "com.antonkuritsyn.dk.tlv"
version = "1.0.0"

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["kotlin"])
            artifactId = "kotlin-tlv-ccc-dk"

            pom {
                name.set("TLV Kotlin Library (CCC DK Support)")
                description.set("TLV serialization/deserialization library with 2-byte tags and up to 64KiB payload support")
                url.set("https://github.com/javster/kotlin-tlv-ccc-dk")

                licenses {
                    license {
                        name.set("Apache-2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("javster")
                        name.set("Anton Kuritsyn")
                        email.set("anton.kuritsin@gmail.com")
                    }
                }

                scm {
                    connection.set("scm:git:github.com/javster/kotlin-tlv-ccc-dk.git")
                    developerConnection.set("scm:git:github.com/javster/kotlin-tlv-ccc-dk.git")
                    url.set("https://github.com/javster/kotlin-tlv-ccc-dk")
                }
            }
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}