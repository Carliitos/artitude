package art.projects.artitude.Models;

/**
 * Created by manel on 9/5/2017.
 */

public class cards {
    private String userId;
    private String name;
    private String profileImageUrl;
    private String postid;
    public cards (String userId, String name, String profileImageUrl,String postid){
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.postid = postid;
    }
    public String getPostId(){return postid;}
    public String getUserId(){
        return userId;
    }
    public void setUserID(String userID){
        this.userId = userId;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getProfileImageUrl(){
        return profileImageUrl;
    }
    public void setProfileImageUrl(String profileImageUrl){
        this.profileImageUrl = profileImageUrl;
    }
}
