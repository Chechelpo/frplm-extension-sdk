# Extension SDK

A series of interfaces that let you interact with the main engine ( https://github.com/Chechelpo/FRPLM )

# Usage

There are two ways to go about writing an extension. There's the helper way (painful) with the npm package ( and the manual way (hell).

## Helper (recommended)
Run under your project directory:
```
npm exec --yes --package @frplm/frontend-build-helper@latest -- create-frplm-extension <extension-id>
```
Replace `<extension-id>` with your particular extension id (name it yourself). Depending on your extension id, you might need to change the resulting package name to a valid Java package name.

Helper repository: https://github.com/Chechelpo/frplm-ext-build-helper 

## Manual (pain)

1. Start a maven project.

2. Declare the sdk as dependencies on your extension's pom.xml
```
        <dependency>
            <groupId>io.github.chechelpo.frplm</groupId>
            <artifactId>frplm-extension-api</artifactId>
            <version>${frplm.extension.sdk.version}</version>
            <scope>compile</scope>
        </dependency>
```
3. Register the plugin to inform the engine of the main extension class location: 
```
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.15.0</version>
                <configuration>
                    <release>${maven.compiler.release}</release>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>io.github.chechelpo.frplm</groupId>
                            <artifactId>frplm-annotations-processor</artifactId>
                            <version>${frplm.extension.sdk.version}</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
```
