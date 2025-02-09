plugins {
	id("java")
}

group = "org.ham-study"
version = "1.0-SNAPSHOT"

repositories {
	mavenCentral()
}

dependencies {
	implementation("ch.qos.logback:logback-classic:1.4.5")
	implementation("com.google.guava:guava:31.1-jre")
	implementation("org.apache.tomcat.embed:tomcat-embed-core:10.1.5")
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
	testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.1")
	testImplementation("org.assertj:assertj-core:3.24.2")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
	useJUnitPlatform()
}
