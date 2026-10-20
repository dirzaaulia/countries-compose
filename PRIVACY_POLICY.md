# Privacy Policy for Countries Compose

**Effective Date:** October 11, 2026  
**Last Updated:** October 11, 2026

Welcome to **Countries Compose** ("we", "our", or "us"), developed by **Dirza Aulia** ([dirzaaulia.com](https://dirzaaulia.com)). This Privacy Policy explains how our application handles information across Android and WebAssembly (WASM) platforms.

---

## 1. Zero Personal Data Collection
We prioritize user privacy. **Countries Compose does NOT collect, store, transmit, or share any personally identifiable information (PII).**

* **No User Accounts:** You do not need to register, log in, or provide an email address to use this application.
* **No Analytics Tracking:** We do not track individual user behavior or build user profiles.
* **No Targeted Advertising:** We do not collect device advertising identifiers (`AD_ID`) or display targeted advertisements.

---

## 2. Network Requests & Third-Party APIs
To provide real-time 3D planetary telemetry, weather forecasts, seismic monitoring, and macroeconomic stats, Countries Compose connects directly to open scientific data providers. 

When you access these features, your device makes direct HTTP requests to the following endpoints:
* **REST Countries API** (`restcountries.com`): Country facts, borders, and demographics.
* **Open-Meteo API** (`open-meteo.com`): Meteorology forecasts and weather conditions.
* **USGS Earthquake API** (`earthquake.usgs.gov`): Real-time seismic activity.
* **NASA EONET API** (`eonet.gsfc.nasa.gov`): Global natural hazard events.
* **NOAA SWPC** (`swpc.noaa.gov`): Space weather and auroral oval predictions.
* **World Bank API** (`data.worldbank.org`): Macroeconomic indicators.
* **CelesTrak / N2YO** (`celestrak.org`): Satellite orbital elements.
* **FlagCDN** (`flagcdn.com`): High-resolution vector flag graphics.

*Note: These third-party scientific APIs receive standard network request metadata (such as IP address and User-Agent) necessary to fulfill HTTP responses. None of your personal data is sent to these services.*

---

## 3. Local In-Memory Caching
To ensure smooth 60 FPS performance and reduce network bandwidth, the app maintains an **in-memory cache** on your device for session data (such as weather forecasts and country details). This cache is temporary and cleared when the application is closed.

---

## 4. Google Play Feature Delivery
On Android, optional expansion features (such as Mars Expedition, Deep Space Gas Giants, and Satellite Tracking) are delivered via Google Play Feature Delivery. Downloading these modules is entirely optional and initiated only upon explicit user consent.

---

## 5. Contact & Support
If you have any questions or feedback regarding this Privacy Policy, please contact the developer:
* **Website:** [https://dirzaaulia.com](https://dirzaaulia.com)
* **Support / Ko-fi:** [https://ko-fi.com/dirzaaulia](https://ko-fi.com/dirzaaulia)
* **Support / Saweria:** [https://saweria.co/dirzaaulia](https://saweria.co/dirzaaulia)
