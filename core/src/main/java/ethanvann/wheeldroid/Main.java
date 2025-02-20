package ethanvann.wheeldroid;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import ethanvann.wheeldroid.entities.Asteroid;
import ethanvann.wheeldroid.entities.Bullet;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter implements InputProcessor {
    private SpriteBatch batch;
    private Texture image;
    private Texture bullet;
    private Texture ship;
    private Texture gameover;
    private Texture station;
    private Texture meteor;
    boolean lost = false;
    private float angle =0;
    private float timeSeconds=0;
    private float shotTimer = 0.2f;
    private Timer asteroidTimer;
    private OrthographicCamera cam;
    private ArrayList<Bullet> bullets = new ArrayList();
    List<Asteroid> asteroids = Collections.synchronizedList(new ArrayList<Asteroid>());
    @Override
    public void create() {
        asteroidTimer =new Timer();
        asteroidTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                asteroids.add(new Asteroid((float) (Math.random()*360f),System.currentTimeMillis(), (int) (Math.random()*9f+1)));
            }
        },2,2);
        batch = new SpriteBatch();
        meteor = new Texture("meteor.png");
        ship = new Texture("ship.png");
        bullet = new Texture("bullet.png");
        image = new Texture("libgdx.png");
        station = new Texture("station.png");
        gameover = new Texture("gameover.png");
        Gdx.input.setInputProcessor(this);
        cam = new OrthographicCamera(800, 800);
        cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
        cam.update();
    }
    public float angleToCenteredXCoord(float angle,float multiplier){
        return (float) (Math.cos(Math.toRadians(angle+90%360))*multiplier+cam.viewportWidth / 2f);
    }
    public float angleToCenteredYCoord(float angle,float multiplier){
        return (float) (Math.sin(Math.toRadians(angle+90%360))*multiplier+cam.viewportHeight / 2f);
    }
    @Override
    public void render() {
        float shipX = angleToCenteredXCoord(angle,140);
        float shipY = angleToCenteredYCoord(angle,140);
        timeSeconds +=Gdx.graphics.getDeltaTime();
        if(timeSeconds > shotTimer){
            timeSeconds-=shotTimer;
            shoot();
        }
        cam.update();
        batch.setProjectionMatrix(cam.combined);
        ScreenUtils.clear(0, 0, 0, 1f);
        batch.begin();
        if(lost){
            drawCenteredWithRotation(batch,gameover,cam.viewportWidth / 2f,cam.viewportHeight / 2f,0,0.5f);
            batch.end();
            return;
        }
        drawCenteredWithRotation(batch,station,cam.viewportWidth / 2f,cam.viewportHeight / 2f,0,1.2f);
        drawCenteredWithRotation(batch,ship,shipX, shipY,angle,0.4f);
        Iterator<Bullet> bulletIt = bullets.iterator();
        while (bulletIt.hasNext()){
            Bullet bullet = bulletIt.next();
            float bulletX = bullet.getX(cam);
            float bulletY = bullet.getY(cam);
            if(bulletX<=0||bulletX>=cam.viewportWidth||bulletY<=0||bulletY>=cam.viewportHeight){
                bulletIt.remove();
                continue;
            }
            Iterator<Asteroid> asteroidIterator = asteroids.iterator();
            boolean skip = false;
            while (asteroidIterator.hasNext()){
                Asteroid asteroid = asteroidIterator.next();
                boolean hit;
                float asteroidX = asteroid.getX(cam);
                float asteroidY = asteroid.getY(cam);
                hit = intersect(bulletX+(bullet.getSize()/2),bulletY+(bullet.getSize()/2),bulletX-(bullet.getSize()/2),bulletY-(bullet.getSize()/2),asteroidX+(asteroid.getSize()/2),asteroidY+(asteroid.getSize()/2),asteroidX-(asteroid.getSize()/2),asteroidY-(asteroid.getSize()/2));
//                if (bulletX-bullet.getSize()/2 < asteroid.getX(cam)-asteroid.getSize()/2 + asteroid.getSize() && asteroid.getX(cam)-asteroid.getSize()/2 < bulletX-bullet.getSize()/2 + bullet.getSize() && bulletY-bullet.getSize()/2 < asteroid.getY(cam)-asteroid.getSize()/2 + asteroid.getSize())
//                    hit =  asteroid.getY(cam)-asteroid.getSize()/2 < bulletY-bullet.getSize()/2 + bullet.getSize();
//                else
//                   hit = false;
                if(hit) {
                    asteroid.damage(1);
                    bulletIt.remove();
                    skip = true;
                    if (asteroid.getHp() == 0) {
                        asteroidIterator.remove();
                    }
                    break;
                }
            }
            if(skip){
                continue;
            }
            drawCenteredWithRotation(batch, this.bullet,bulletX, bulletY,bullet.getRotation(),0.4f);
        }
        Iterator<Asteroid> asteroidIterator = asteroids.iterator();
        while (asteroidIterator.hasNext()){
            Asteroid asteroid = asteroidIterator.next();
            drawCenteredWithRotation(batch,meteor,asteroid.getX(cam), asteroid.getY(cam),asteroid.getRotation(),0.4f* asteroid.getHp());
            float asteroidX = asteroid.getX(cam);
            float asteroidY = asteroid.getY(cam);
            float stationX = cam.viewportWidth / 2f;
            float stationY = cam.viewportHeight / 2f;
            float stationSize = 128;
            lost = intersect(stationX+(stationSize/2),stationY+(stationSize/2),stationX-(stationSize/2),stationY-(stationSize/2),asteroidX+(asteroid.getSize()/2),asteroidY+(asteroid.getSize()/2),asteroidX-(asteroid.getSize()/2),asteroidY-(asteroid.getSize()/2));
            if(lost){
                batch.end();
                return;
            }
        }
        batch.end();
    }
    public void shoot(){
        bullets.add(new Bullet(angle,System.currentTimeMillis()));
    }
    public boolean intersect(float r1TopRightX,float r1TopRightY,float r1BottomLeftX,float r1BottomLeftY,float r2TopRightX,float r2TopRightY,float r2BottomLeftX,float r2BottomLeftY) {
        if (r1TopRightY < r2BottomLeftY || r1BottomLeftY > r2TopRightY) {
            return false;
        }
        if (r1TopRightX < r2BottomLeftX || r1BottomLeftX > r2TopRightX) {
            return false;
        }
        return true;
    }

    @Override
    public void resize(int width, int height) {
        System.out.println("resized");
        cam.viewportWidth = 800f;
        cam.viewportHeight = 800f;
        cam.update();
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }

    public void drawCenteredWithRotation(SpriteBatch batch, Texture texture,float x,float y,float rotation,float scale){
        batch.draw(texture, x- (float) texture.getWidth() /2, y- (float) texture.getHeight() /2, (float) texture.getWidth() /2, (float) texture.getHeight() /2, texture.getWidth(), texture.getHeight(), scale, scale,rotation,0,0, texture.getTextureData().getWidth(), texture.getTextureData().getHeight(),false, false);
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        angle+=amountY*10;
        return false;
    }
}
