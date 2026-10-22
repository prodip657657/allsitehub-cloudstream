# AllSiteHub for CloudStream (directory plugin, beta)

এটি **CloudStream-এর জন্য website-directory plugin project**। AllSiteHub-এর ৮টি ক্যাটাগরি এবং বর্তমানে তালিকাভুক্ত ৮০টি সাইটের fallback snapshot রয়েছে। Plugin নিজে AllSiteHub-এর live HTML page থেকে নতুন external website links পড়ার চেষ্টা করে।

**সীমাবদ্ধতা:** AllSiteHub একটি link directory, ভিডিও host নয়। তাই CloudStream-এ site names **দেখা ও search** করা যাবে, কিন্তু arbitrary third-party website-এর ভেতরে ঢুকে সব page browse করা বা তাদের video play করা যাবে না। `loadLinks` ইচ্ছাকৃতভাবে কোনো ভুয়া stream দেয় না। বাইরের ওয়েবসাইটগুলো খুলতে `allsitehub_directory.html` সাধারণ ব্রাউজারে ব্যবহার করুন। প্রতিটি playable site-এর জন্য আলাদা provider/extractor লাগবে।

## GitHub-এ আপলোড করে CloudStream-এ যুক্ত করবেন যেভাবে

1. GitHub-এ **public** repository তৈরি করুন, যেমন `allsitehub-cloudstream`।
2. এই ZIP-এর ফাইল/ফোল্ডারগুলো নতুন repo-র root-এ **Upload files** করুন; root-এর ভেতরে আরেকটি একই নামের folder যেন না থাকে।
3. GitHub repo-র **Actions** tab খুলুন। Workflow permission `Read and write permissions` দিন: Settings → Actions → General → Workflow permissions (প্রয়োজন হলে)।
4. Actions → **Build and publish CloudStream extension** → **Run workflow**। Main branch-এ push করলেও এটি চালু হবে। প্রথম run সবুজ হলে `builds` branch-এ `repo.json`, `plugins.json`, `.cs3` তৈরি হবে।
5. CloudStream → Settings → Extensions → **Add repository**-তে দিন:

   `https://raw.githubusercontent.com/YOUR_USERNAME/allsitehub-cloudstream/builds/repo.json`

   `YOUR_USERNAME`-এর জায়গায় নিজের GitHub username বসাবেন। Repo অন্য নামে বানালে URL-এ সেই নামও বসাবেন।
6. নতুন repository খুলে **AllSiteHubProvider** extension install করুন, তারপর provider selector-এ **AllSiteHub • Website Directory** বেছে নিন।

## আচরণ

- Home categories: Movies & Shows, Anime, Manga, Live TV & Sports, Paid, Apps, Download, Games।
- Search domain বা category দিয়ে কাজ করার জন্য লেখা হয়েছে।
- Source page HTML থেকে fresh links parse করার চেষ্টা করে, ৩০ মিনিট cache করে। HTML বদলালে বা সাইট unreachable হলে snapshot তালিকা ব্যবহার করে।
- Site details-এ external web URL text দেখায়। কোনো DRM bypass, scraping of third-party content, fake media stream, login bypass নেই।

## Build details

GitHub Actions ব্যবহার করলে স্থানীয় Android Studio বা Gradle ইনস্টল করা লাগবে না। Local build করতে হলে JDK 17, Android SDK API 35 ও Gradle 8.10.2 দরকার; কমান্ড: `gradle make makePluginsJson`।

**Status:** Source code + catalog + CI workflow প্রস্তুত। এই পরিবেশে networked Gradle dependencies/Android emulator পাওয়া যায়নি, তাই `.cs3` compile/run **এখনো যাচাই করা হয়নি**। Actions job সফল না হওয়া পর্যন্ত CloudStream-এ যোগ করা যাবে না।

## Security

AllSiteHub-এর কিছু third-party destination সন্দেহজনক/অননুমোদিত হতে পারে। Links সুপারিশ/নিরাপত্তার নিশ্চয়তা নয়। লগইন, payment, APK download বা personal information দেওয়ার আগে যাচাই করুন।
