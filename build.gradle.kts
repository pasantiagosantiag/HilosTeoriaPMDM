plugins {
    id("java")
application
   // id("org.bytedeco.gradle-javacpp-platform") version ("1.5.10")
}

group = "ies.sequeros.smr.game.bobble"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
application {
    mainClass.set("ies.sequeros.dam.psp.threads.patrones.pipeline.Principal")
}
dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    //implementation("org.bytedeco:openblas-platform:0.3.31-1.5.13")
    //implementation("org.bytedeco:opencv-platform:4.13.0-1.5.13")
   // implementation("org.bytedeco:javacv-platform:1.5.13")
    implementation("org.openpnp:opencv:4.9.0-0")

   // implementation("org.bytedeco:javacv-platform:1.5.13")
   // implementation("org.bytedeco:openblas-platform:0.3.31-1.5.13")

    //runtimeOnly("org.bytedeco:opencv:4.13.0-1.5.13:windows-x86_64")
}

tasks.test {
    useJUnitPlatform()
}