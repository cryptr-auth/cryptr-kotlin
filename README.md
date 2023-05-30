# cryptr-kotlin

This Kotlin library provides tools for Cryptr API

## Documentation

See the [Cryptr API Reference](https://docs.cryptr.co)

## Installation

### Apache Maven

```xml

<dependency>
    <groupId>co.cryptr</groupId>
    <artifactId>cryptr-kotlin</artifactId>
    <version>0.1.3</version>
</dependency>

```

### Gradle (Groovy DSL)

```groovy

dependencies {
    implementation 'co.cryptr:cryptr-kotlin:0.1.3'
}

```

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("co.cryptr:cryptr-kotlin:0.1.3")
}
```

## Installation

```kotlin

import cryptr.kotlin.Cryptr

// if you use system properties you call just init like this
val cryptr = Cryptr()

// If you prefer to define them manually
val cryptr = Cryptr(
    tenantDomain = "my-saas-company",
    apiKeyClientId = "api-key-id",
    apiKeyClientSecret = "api-key-secret"
)

// You can also specify your cryptr service url
val cryptr = Cryptr(
    tenantDomain = "my-saas-company",
    baseUrl = "https://my-saas-company.authent.me",
    apiKeyClientId = "api-key-id",
    apiKeyClientSecret = "api-key-secret"
)
```

You can also define your Configuration through [System properties](#system-property-keys)

### SSO SAML Headless process

This process allows you to generate a challenge to start a SSO SAML authentication process without using a front-end for
the
entire process

```kotlin

// 1. generate a challenge from any point of your app (requires network) and retrieve authorization URL
val ssoSamlChallengePayload =
    cryptr.createSsoSamlChallenge(
        redirectUri = "https://localhost:8080/some-callback-endpoint",
        orgDomain = orgDomain,
        userEmail = userEmail
    )

if (ssoSamlChallengePayload is APISuccess) {
    val authorizationUrl = ssoSamlChallengePayload.value.authorizationUrl
}

// 2. Give this authorization URL to the end-user (ex: by email or just by a redirection)

// 3. handle the redirection on the chosen enpoint (here '/some-callback-endpoint)
// on this enpoint you get a `code` parameter
val challengeValidation = cryptr.validateSsoChallenge(call.parameters.get("code"))
if (challengeValidation is APISuccess) {
    val endUserAccessToken = challengeValidation.value.accessToken
}
```

## System property keys

| key                              | Required | Default value          | sample value                      | purpose                                                      |
|----------------------------------|----------|------------------------|-----------------------------------|--------------------------------------------------------------|
| **CRYPTR_ACCOUNT_DOMAIN**        | true     | None                   | `your-tenant-domain`              | Your Account domain                                          |
| **CRYPTR_BASE_URL**              | false    | https://auth.cryptr.eu | `https://company.authent.me`      | Your Cryptr service URL                                      |
| **CRYPTR_DEFAULT_REDIRECT_URL**  |          |                        | `https://localhost:8080/callback` | The URL where to redirect end-user after SSO authent process |
| **CRYPTR_API_KEY_CLIENT_ID**     | true     | None                   | `xxx`                             | Your API Key client ID                                       |
| **CRYPTR_API_KEY_CLIENT_SECRET** | true     | None                   | `xxx`                             | Your API Key client Secret                                   |