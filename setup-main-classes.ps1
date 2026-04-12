$basePath = "A:\Projects\foodAi"

$services = @(
    "restaurant-service",
    "order-service",
    "ai-service",
    "notification-service",
    "api-gateway"
)

foreach ($service in $services) {

    $pomPath = "$basePath\$service\pom.xml"

    if (!(Test-Path $pomPath)) {
        Write-Host "Skipping $service (pom.xml not found)"
        continue
    }

    Write-Host "Fixing $service..."

    $content = Get-Content $pomPath -Raw

    # Ensure <dependencies> exists
    if ($content -notmatch "<dependencies>") {
        $content = $content -replace "</project>", "<dependencies></dependencies>`n</project>"
    }

    # Add Spring Boot dependency
    if ($content -notmatch "spring-boot-starter-web") {
        $dependency = "<dependency>`n" +
                      "    <groupId>org.springframework.boot</groupId>`n" +
                      "    <artifactId>spring-boot-starter-web</artifactId>`n" +
                      "</dependency>`n"

        $content = $content -replace "</dependencies>", "$dependency</dependencies>"
    }

    # Add Spring Boot plugin
    if ($content -notmatch "spring-boot-maven-plugin") {

        $plugin = "<build>`n" +
                  "    <plugins>`n" +
                  "        <plugin>`n" +
                  "            <groupId>org.springframework.boot</groupId>`n" +
                  "            <artifactId>spring-boot-maven-plugin</artifactId>`n" +
                  "        </plugin>`n" +
                  "    </plugins>`n" +
                  "</build>"

        if ($content -notmatch "<build>") {
            $content += "`n$plugin"
        }
    }

    # Write without BOM
    [System.IO.File]::WriteAllText($pomPath, $content, (New-Object System.Text.UTF8Encoding($false)))

    Write-Host "$service updated"
}

Write-Host "DONE"