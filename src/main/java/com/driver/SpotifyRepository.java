package com.driver;

import java.util.*;

//import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user = new User(name, mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
    Artist artist = new Artist(name);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
         Artist artist = artists.stream()
        .filter(a -> a.getName().equals(artistName))
        .findFirst()
        .orElseGet(() -> createArtist(artistName));
        Album album = new Album(title);
        album.add(album);
        artistAlbumMap.putIfAbsent(artist, new ArrayList<>());
        artistAlbumMap.get(artist).add(album);
        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
         Album album = albums.stream()
      .filter(a -> a.getTitle().equals(albumName))
      .findFirst()
      .orElseThrow(() -> new Exception("Album  does not exist"));
      Song song = new Song(title, length);
      song.add(sonng);
      albumSongMap.get(album, new ArrayList<>());
      albumSongMap.get(album).add(song);
      return song;  
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
 User user = users.stream()
        .filter(u -> u.getMobile().equals(mobile))
        .findFirst()
        .orElseThrow(() -> new Exception("User does not exist"));
        Playlist playlist = new Playlist(title);
        playlist.add(playlist);
        List<Song> songsWithLength = songs.stream()
        .filter(s -> s.getLength() == length)
        .toList();
        playlistSongMap.put(playlist, songsWithLength);
        playlistListenerMap.put(playlist, new ArrayList<>(List.of(user)));
        creatorPlaylistMap.put(user, playlist);
        userPlaylistMap.putIfAbsent(user, new ArrayList<>());
        userPlaylistMap.get(user).add(playlist);
        return playlist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
       User user = users.stream()
                .filter(u -> u.getMobile().equals(mobile))
                .findFirst()
                .orElseThrow(() -> new Exception("User  does not exist"));

        Playlist playlist = new Playlist(title);
        playlists.add(playlist);
        List<Song> songsToAdd = songs.stream()
                .filter(s -> songTitles.contains(s.getTitle()))
                .toList();
        playlistSongMap.put(playlist, songsToAdd);
        playlistListenerMap.put(playlist, new ArrayList<>(List.of(user)));
        creatorPlaylistMap.put(user, playlist);
        userPlaylistMap.putIfAbsent(user, new ArrayList<>());
        userPlaylistMap.get(user).add(playlist);
        return playlist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
       User user = users.stream()
        .filter(u -> u.getMobile().equals(mobile))
        .findFirst()
        .orElseThrow(() -> new Exception("User  does not exist"));

Playlist playlist = playlists.stream()
        .filter(p -> p.getTitle().equals(playlistTitle))
        .findFirst()
        .orElseThrow(() -> new Exception("Playlist does not exist"));

// Check if the user is already a listener or the creator
     if (!playlistListenerMap.get(playlist).contains(user) && 
         !creatorPlaylistMap.get(user).equals(playlist)) {
       playlistListenerMap.get(playlist).add(user);
    }

return playlist;

    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
              User user = users.stream()
        .filter(u -> u.getMobile().equals(mobile))
        .findFirst()
        .orElseThrow(() -> new Exception("User  does not exist"));

Song song = songs.stream()
        .filter(s -> s.getTitle().equals(songTitle))
        .findFirst()
        .orElseThrow(() -> new Exception("Song does not exist"));

// Check if the user has already liked the song
if (!songLikeMap.containsKey(song) || !songLikeMap.get(song).contains(user)) {
    songLikeMap.putIfAbsent(song, new ArrayList<>());
    songLikeMap.get(song).add(user);
    song.setLikes(song.getLikes() + 1);

    // Like the corresponding artist
    Artist artist = artistAlbumMap.entrySet().stream()
            .filter(entry -> entry.getValue().stream()
                    .anyMatch(album -> albumSongMap.get(album).contains(song)))
            .map(Map.Entry::getKey)
            .findFirst()
            .orElse(null);

    if (artist != null) {
        artist.setLikes(artist.getLikes() + 1);
    }
  }

return song;  
    }

    public String mostPopularArtist() {
         return artists.stream()
                .max(Comparator.comparingInt(Artist::getLikes))
                .map(Artist::getName)
                .orElse(null);
    }

    public String mostPopularSong() {
        return songs.stream()
        .max(Comparator.comparingInt(Song::getLikes))
        .map(Song::getTitle)
        .orElse(null);  
    }
}
