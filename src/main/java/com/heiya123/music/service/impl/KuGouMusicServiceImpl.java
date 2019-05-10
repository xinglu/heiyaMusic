package com.heiya123.music.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.heiya123.music.common.RequestHeaders;
import com.heiya123.music.constant.MusicConstant;
import com.heiya123.music.musicEnum.MusicType;
import com.heiya123.music.service.MusicService;
import com.heiya123.music.util.CommonUtils;
import com.heiya123.music.util.OkHttpUtils;
import com.heiya123.music.vo.Music;
import com.heiya123.music.vo.MusicReqVo;
import com.heiya123.music.vo.kugou.KuGouMusicBase;
import com.heiya123.music.vo.kugou.KuGouMusicBase.DataBean.ListsBean;
import com.heiya123.music.vo.kugou.KuGouMusicUrl;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service("KugouMusic")
public class KuGouMusicServiceImpl implements MusicService {

  /**
   * 分页获取歌曲
   */
  @Override
  public List<Music> getMusics(MusicReqVo req) {
    ArrayList<Music> list = new ArrayList<>();
    String url = String
        .format(MusicConstant.KUGOUMUSIC_SEARCH, req.getName(), req.getPageNo(), req.getPageSize());
    KuGouMusicBase kuGouMusicBase = OkHttpUtils
        .getRequest(url, RequestHeaders.KUGOUHEADERS, null, KuGouMusicBase.class);
    List<ListsBean> lists = kuGouMusicBase.getData().getLists();
    String emptyKey = "00000000000000000000000000000000";
    for (ListsBean song : lists) {
      Music music = new Music();
      music.setArtist(song.getSingerName());
      music.setName(song.getSongName());
      String hash =
          emptyKey.equals(song.getSQFileHash()) ? (emptyKey.equals(song.getHQFileHash()) ? song
              .getFileHash() : song.getHQFileHash()) : song.getSQFileHash();
      music.setId(hash);
      music.setPic_id(hash);
      music.setUrl_id(hash);
      music.setLyric_id(hash);
      music.setAlbum(song.getAlbumName());
      music.setType(MusicType.KUGOU);
      list.add(music);
    }
    return list;
  }

  /**
   * 获取封面图片
   */
  @Override
  public String getPic(String hash) {
    String url = String.format(MusicConstant.KUGOUMUSIC_PIC, hash);
    String rsp = OkHttpUtils.getRequest(url, RequestHeaders.KUGOUHEADERS);
    JSONObject jsonObject = JSON.parseObject(rsp);
    return (String) jsonObject.get("url");
  }

  /**
   * 获取歌曲链接
   */
  @Override
  public String getMusicUrl(String hash) {
    String url = String
        .format(MusicConstant.KUGOUMUSIC_URL, hash, CommonUtils.getMD5(hash + "kgcloudv2"));
    KuGouMusicUrl kuGouMusic = OkHttpUtils
        .getRequest(url, RequestHeaders.KUGOUHEADERS, null, KuGouMusicUrl.class);
    return kuGouMusic.getUrl();
  }

  /**
   * 获取歌词
   */
  @Override
  public String getLyric(String hash) {
    String url = String.format(MusicConstant.KUGOUMUSIC_LYRIC, hash);
    return OkHttpUtils.getRequest(url, RequestHeaders.KUGOUHEADERS);
  }
}
