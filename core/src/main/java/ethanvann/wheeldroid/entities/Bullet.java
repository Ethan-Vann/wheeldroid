package ethanvann.wheeldroid.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;

public class Bullet {
    float rotation;
    long time;
    static int nextID = 0;
    static int size = 25;
    static float speed =-5;
    static float spawnRadius = 160;

    int id;
    public Bullet(float rotation,long time){
        id = nextID;
        nextID++;
        this.rotation = rotation;
        this.time = time;
    }

    public float getRotation() {
        return rotation;
    }

    public float getSize(){
        return size;
    }
    public long getTime() {
        return time;
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
