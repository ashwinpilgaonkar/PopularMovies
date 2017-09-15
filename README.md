# Popular Movies: Stage 2
![Popular Movies](https://github.com/ashwinpilgaonkar/PopularMovies/blob/master/Screenshots/popmovies_icon.jpg)  
**Udacity Android Developer Nanodegree: Project 2**  
A simple app that shows a grid of popular movies. Created for Udacity's Android App Development Nanodegree Program.

# Usage
* `Git clone` the repo and import it in Android Studio
* Obtain your own API Key from https://www.themoviedb.org/
* Add your key to the field marked **API_KEY** in **gradle.properties**
* **WARNING:** The app will crash on startup if the API Key is not added
* `minSdkVersion 16`

# Features
* Displays a grid of movie posters upon launch
* Movies can either be sorted by **Popularity** or **Rating**
* The sort order can be by most popular or by highest-rated
* Tapping on a movie poster displays additional details on a new screen
* Movies can be added to a favorites list which can be viewed offline
* Movie trailers can be played and shared
* Comes with a light and a dark theme
* Supports multi-pane layouts for tablets

# Implementation
* Fully adheres to [Material Design](https://material.io/guidelines/) guidelines
* Followed good programming practices such as adding all strings to strings.xml and dimensions to dimens.xml 
* Used fragments to add support for devices with larger screen sizes
* Made app production ready by taking care of all edge cases and handling exceptions
* Followed MVC pattern to deal with REST APIs

# Libraries Used
* [Butterknife](https://github.com/JakeWharton/butterknife)
* [Picasso](https://github.com/square/picasso)
* [Volley](https://github.com/google/volley)

# Screenshots
### Phone
![PhoneUI MainPage](https://github.com/ashwinpilgaonkar/PopularMovies/blob/master/Screenshots/phone-main-portrait.jpg)  
![PhoneUI DetailPage Black](https://github.com/ashwinpilgaonkar/PopularMovies/blob/master/Screenshots/phone-detail-portrait-black.jpg)
![PhoneUI DetailPage White](https://github.com/ashwinpilgaonkar/PopularMovies/blob/master/Screenshots/phone-detail-portrait-white.jpg)

### 7" Tablet
![7"TabletUI DetailPage Landscape](https://github.com/ashwinpilgaonkar/PopularMovies/blob/master/Screenshots/tab7-land.jpg)

### 10" Tablet
![10"TabletUI MainPage Landscape](https://github.com/ashwinpilgaonkar/PopularMovies/blob/master/Screenshots/tab10-land.jpg)
