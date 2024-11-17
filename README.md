# Video Games System

This Android application provides a system for browsing, searching, and managing video games. The app allows users to explore games by their ratings, release dates, or search queries. Users can also add games to their favorite list and view detailed descriptions of each game.

## Features

- **Games List**: Browse games based on various criteria such as Top Rated or Newest Games.
- **Search Games**: Search games by specific criteria, displaying results dynamically.
- **Favorites**: Users can mark their favorite games, which are saved in their profile.
- **Game Details**: View detailed descriptions for each game.
- **Responsive Design**: Optimized for smooth user experience on various Android devices.

## Architecture

The application is structured using the following key components:

- **Fragments**: To display dynamic content (games list, game details).
- **RecyclerView**: For efficient display of lists of games, enabling smooth scrolling.
- **Adapters**: Used to bind data to views in the RecyclerView.
- **Firebase**: Used to store and retrieve user data, including favorite games.
- **API Integration**: The app fetches game data from the [RAWG API](https://rawg.io/) using HTTP requests.

## Dependencies

- **Firebase**: For user authentication and storing user data.
- **Gson**: For parsing JSON responses from the API.
- **Glide**: For image loading and caching.
- **RecyclerView**: For displaying lists of games.
- **StrictMode**: To allow network operations on the main thread (for simplicity in this case).

## Usage

### Main Features

#### Games List:
- The user can browse a list of games categorized by "Top Rated" or "Newest".
- A `RecyclerView` is used to efficiently display a list of games.

#### Game Details:
- Click on a game to view its description.
- An `AlertDialog` is used to display the game's details.

#### Search:
- The app allows users to search for games using a query and filters.
- The search results are dynamically displayed using another `RecyclerView`.

#### Favorites:
- Users can mark a game as a favorite.
- Favorites are saved in Firebase and can be retrieved at any time.

#### User Authentication:
- The app supports user authentication using Firebase.
- Users can log in, and their favorites will be tied to their accounts.
