import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0" // 使用 Kotlin 1.9.0
}

group = "org.llvm4j"
version = "0.1.1-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    api("org.bytedeco:llvm-platform:13.0.1-1.5.7")
    implementation("org.bytedeco:llvm-platform:13.0.1-1.5.7")
    api("org.llvm4j:optional:0.2.0-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.0") // 使用 Kotlin 1.9.0

    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.0") // 使用 Kotlin 1.9.0
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0") // 使用 JUnit 5.9.0
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    withType<Test>().configureEach {
        useJUnitPlatform()
        testLogging {
            afterTest(
                KotlinClosure2<TestDescriptor, TestResult, Unit>({ desc, result ->
                    val resultText = result.resultType.name.padEnd(7, ' ')
                    val timeText = (result.endTime - result.startTime).toString().padStart(4, '0')
                    println("$resultText | ($timeText ms) | ${desc.className} :: ${desc.name}")
                })
            )
        }
    }

    val jar by getting(Jar::class) {
        archiveBaseName.set("llvm4j")
        archiveVersion.set(version)
        manifest {
        }
        from(sourceSets["main"].output)
        from(configurations.runtimeClasspath.get().filter { it.name.contains("optional") || it.name.contains("kotlin-stdlib-jdk8") }.map { zipTree(it) })
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

}

task("fatJar", type = Jar::class) {
    group = "build"
    archiveBaseName.set("llvm4j")
    archiveVersion.set(version.toString())
    manifest {
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}