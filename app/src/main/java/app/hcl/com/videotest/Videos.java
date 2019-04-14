package app.hcl.com.videotest;

import android.provider.MediaStore;

import java.io.Serializable;

public class Videos  implements Serializable{

    String url,title,image;
    Videos(String url,String title,String image)
    {
        this.url=url;
        this.title=title;
        this.image=image;
    }

    Videos(String url,String title)
    {
        this.url=url;
        this.title=title;

    }
}
