# Google Play Store Publishing Guide for Cafe Coffee App

This guide walks you step-by-step through generating your signed release bundle and submitting **Cafe Coffee App** to the Google Play Store.

---

## 1. Firebase Console (Link New Package Name)

Because we updated the package name to `com.cafecoffee.app`:
1. Open your [Firebase Console](https://console.firebase.google.com/) and click your project **`cafecoffeeapp`**.
2. Click **Project Settings** (gear icon at top left).
3. Scroll down to **Your apps**, click **Add app** ➔ choose **Android** icon.
4. Enter the package name:
   ```
   com.cafecoffee.app
   ```
5. Click **Register app** ➔ download the updated `google-services.json` and replace it in `app/google-services.json`.

---

## 2. Generate Signed Android App Bundle (`.aab`) in Android Studio

Google Play requires an `.aab` file signed with a private upload key:

1. In Android Studio, open the top menu: **Build** ➔ **Generate Signed Bundle / APK...**
2. Choose **Android App Bundle** and click **Next**.
3. Under **Key store path**, click **Create new...**:
   - **Key store path:** Choose a safe folder on your computer (e.g., `Documents/cafecoffee-keystore.jks`).
   - **Password:** Choose a secure password (e.g., `CoffeePass2026`).
   - **Alias:** `cafecoffee`
   - **Validity (years):** `25` or more.
   - Enter your name under First and Last Name.
   - Click **OK**.
4. Click **Next**.
5. Select Build Variant: **`release`**.
6. Check **V1 / V2** signature if prompted and click **Finish**.
7. Android Studio will build your release bundle:
   ```
   app/release/app-release.aab
   ```
   *This is the exact file you upload to Google Play!*

---

## 3. Google Play Console Setup

1. Go to [Google Play Console](https://play.google.com/console) and log in.
2. Click **Create app** (top right):
   - **App name:** `Cafe Coffee App`
   - **Default language:** `English`
   - **App or game:** `App`
   - **Free or paid:** `Free`
   - Accept declarations and click **Create app**.

---

## 4. Complete Store Listing & Assets

In the left menu under **Grow** ➔ **Store presence** ➔ **Main store listing**:

### Text:
- **Short description (80 chars max):**
  > Order handcrafted espressos, lattes, and artisanal brews with quick delivery.
- **Full description:**
  > Welcome to Cafe Coffee App! Indulge in our carefully curated selection of handcrafted coffees, artisan pastries, and refreshing iced beverages.
  >
  > Features:
  > • Search & Category Filtering: Browse by Latte, Macchiato, Cappuccino, and Desserts.
  > • Product Customization: Select your cup size (S, M, L) with real-time price updates.
  > • Favorites: Save your preferred brews for instant reordering.
  > • Fast Cart & Total Breakdown: Automatic delivery fee calculations and GST estimates.
  > • Flexible Checkout: Pay via UPI, Mobile Banking, Cash on Delivery, or Cards.
  > • Real-time Order Tracking: Check current order status from preparation to delivery.

### Graphics:
- **App icon:** 512 × 512 px PNG (Coffee cup logo).
- **Feature graphic:** 1024 × 500 px PNG or JPEG (Coffee promotional image).
- **Phone screenshots:** Take 2 to 4 screenshots from your running app or emulator (Home screen, Product Detail, Cart, Order History).

---

## 5. Policy & App Content

In the left menu under **Policy** ➔ **App content**:
- **Privacy policy:** Host the file [`PRIVACY_POLICY.md`](file:///home/sunbeam/Documents/CafeCoffeeApp/PRIVACY_POLICY.md) on GitHub Pages or Google Sites and paste the public link here.
- **Ads:** Select *"No, my app does not contain ads"*.
- **App access:** Select *"All functionality is available without special access"*.
- **Content rating:** Start questionnaire ➔ select *Utility/Productivity/Communication/Other* ➔ answer the questions ➔ rating will be approved as *Everyone (3+)*.
- **Target audience:** Select *18 and over* (or all ages).

---

## 6. Upload `.aab` & Publish

1. In the left menu, navigate to **Production** (or **Closed testing** if Google Play requires internal testing first).
2. Click **Create new release**.
3. Drag and drop your **`app-release.aab`** file (generated in Step 2).
4. Enter Release name: `1.0 (Initial Release)`.
5. Enter Release notes:
   ```
   Initial launch of Cafe Coffee App! Explore fresh brews, customize sizes, and enjoy seamless order tracking.
   ```
6. Click **Next** ➔ **Save** ➔ **Review release** ➔ **Start rollout to Production**!
