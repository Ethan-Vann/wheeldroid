package ethanvann.wheeldroid.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;

public class Asteroid {
    float rotation;
    long time;
    int hp;
    static int baseSize = 50;
    static float speed = 20f;
    static float spawnRadius = 500f;
    public Asteroid(float rotation,long time,int hp){
        this.rotation = rotation;
        this.time = time;
        this.hp = hp;
    }

    public int getHp() {
        return hp;
    }

    public float getRotation() {
        return rotation;
    }

    public long getTime() {
        return time;
    }
    public float getSize(){
        return baseSize*(0.4f*hp);
    }
    public void damage(int damage){
        hp-=damage;
    }
    public float getX(OrthographicCamera cam){
        long offsetTime = time-System.currentTimeMillis();
        return (float) (Math.cos(Math.toRadians(rotation+90%360))*(spawnRadius+(offsetTime/speed))+cam.viewportWidth / 2f);
    }
    public float getY(OrthographicCamera cam){
        long offsetTime = time-System.currentTimeMillis();
        return (float) (Math.sin(Math.toRadians(rotation+90%360))*(spawnRadius+(offsetTime/speed))+cam.viewportHeight / 2f);
    }
}
