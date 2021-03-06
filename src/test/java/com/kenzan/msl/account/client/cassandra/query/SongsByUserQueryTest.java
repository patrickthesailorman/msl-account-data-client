package com.kenzan.msl.account.client.cassandra.query;

import com.datastax.driver.mapping.MappingManager;
import com.google.common.base.Optional;

import com.kenzan.msl.account.client.TestConstants;
import com.kenzan.msl.account.client.cassandra.QueryAccessor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SongsByUserQueryTest {

  private TestConstants tc = TestConstants.getInstance();
  private long LONG_TIMESTAMP = tc.TIMESTAMP.getTime();

  @Mock
  private QueryAccessor queryAccessor;
  @Mock
  private MappingManager mappingManager;

  @Before
  public void init() {
    queryAccessor = Mockito.mock(QueryAccessor.class);
    when(queryAccessor.songsByUser(tc.USER_ID, tc.TIMESTAMP, tc.SONG_ID)).thenThrow(
        new RuntimeException("TEST_EXPECTED_EXCEPTION"));
  }

  @Test(expected = RuntimeException.class)
  public void testGetSongsByUser() {
    SongsByUserQuery.getUserSong(queryAccessor, mappingManager, tc.USER_ID,
        Long.toString(LONG_TIMESTAMP), tc.SONG_ID);
  }

  @Test
  public void testGetSongListByUser() {
    SongsByUserQuery.getUserSongList(queryAccessor, tc.USER_ID, Optional.absent(),
        Optional.absent());
    verify(queryAccessor, atLeastOnce()).songsByUser(tc.USER_ID);
  }

  @Test
  public void testGetSongListByUserWithLimitAndTimestamp() {
    SongsByUserQuery.getUserSongList(queryAccessor, tc.USER_ID,
        Optional.of(Long.toString(LONG_TIMESTAMP)), Optional.of(tc.LIMIT));
    verify(queryAccessor, atLeastOnce()).songsByUser(tc.USER_ID, tc.TIMESTAMP, tc.LIMIT);
  }

  @Test
  public void testGetSongListByUserWithLimit() {
    SongsByUserQuery.getUserSongList(queryAccessor, tc.USER_ID, Optional.absent(),
        Optional.of(tc.LIMIT));
    verify(queryAccessor, atLeastOnce()).songsByUser(tc.USER_ID, tc.LIMIT);
  }

  @Test
  public void testGetSongListByUserWithTimestamp() {
    SongsByUserQuery.getUserSongList(queryAccessor, tc.USER_ID,
        Optional.of(Long.toString(LONG_TIMESTAMP)), Optional.absent());
    verify(queryAccessor, atLeastOnce()).songsByUser(tc.USER_ID, tc.TIMESTAMP);
  }

  @Test
  public void testAddSongByUser() {
    SongsByUserQuery.add(queryAccessor, tc.SONGS_BY_USER_DTO);
    verify(queryAccessor, atLeastOnce()).addLibrarySong(eq(tc.SONGS_BY_USER_DTO.getUserId()),
        eq(tc.SONGS_BY_USER_DTO.getContentType()), any(Date.class),
        eq(tc.SONGS_BY_USER_DTO.getSongId()), eq(tc.SONGS_BY_USER_DTO.getSongName()),
        eq(tc.SONGS_BY_USER_DTO.getSongDuration()), eq(tc.SONGS_BY_USER_DTO.getAlbumId()),
        eq(tc.SONGS_BY_USER_DTO.getAlbumName()), eq(tc.SONGS_BY_USER_DTO.getAlbumYear()),
        eq(tc.SONGS_BY_USER_DTO.getArtistId()), eq(tc.SONGS_BY_USER_DTO.getArtistMbid()),
        eq(tc.SONGS_BY_USER_DTO.getArtistName()), eq(tc.SONGS_BY_USER_DTO.getImageLink()));
  }

  @Test(expected = Exception.class)
  public void testAddSongByUserException() {
    SongsByUserQuery.add(null, tc.SONGS_BY_USER_DTO);
  }

  @Test
  public void testRemoveSongByUser() {
    SongsByUserQuery.remove(queryAccessor, tc.USER_ID, tc.TIMESTAMP, tc.SONG_ID);
    verify(queryAccessor, atLeastOnce()).deleteLibrarySong(tc.SONG_ID, tc.TIMESTAMP, tc.USER_ID);
  }

  @Test(expected = Exception.class)
  public void testRemoveSongByUserException() {
    SongsByUserQuery.remove(null, tc.USER_ID, tc.TIMESTAMP, tc.SONG_ID);
  }
}
