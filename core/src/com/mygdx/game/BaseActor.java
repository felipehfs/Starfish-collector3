package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;


import java.util.ArrayList;

public class BaseActor extends Actor {
    private Animation<TextureRegion> animation;
    private float elapsedTime;
    private boolean animationPaused;
    private Vector2 velocityVec;
    private Vector2 accelerationVec;
    private float acceleration;
    private float maxSpeed;
    private float deceleration;

    private Polygon boundaryPolygon;
    public static Rectangle worldBounds;


    public BaseActor(float x, float y, Stage stage) {
        super();
        animation = null;
        elapsedTime = 0;
        animationPaused = false;
        setPosition(x, y);
        stage.addActor(this);
        velocityVec = new Vector2(0, 0);
        accelerationVec = new Vector2(0, 0);
        acceleration = 0;

        maxSpeed = 1000;
        deceleration = 0;

    }


    public void setMaxSpeed(float ms) {
        maxSpeed = ms;
    }

    public void setDeceleration(float dec) {
        deceleration = dec;
    }

    public Animation<TextureRegion> loadAnimationFromFiles(String[] filenames, float frameDuration, boolean loop) {
        int fileCount = filenames.length;
        Array<TextureRegion> textureArray = new Array<>();
        for (int n = 0; n < fileCount; n++) {
            String filename = filenames[n];
            Texture texture = new Texture(Gdx.files.internal(filename));
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            textureArray.add(new TextureRegion(texture));
        }

        Animation<TextureRegion> anim = new Animation<>(frameDuration, textureArray);

        if (loop) {
            anim.setPlayMode(Animation.PlayMode.LOOP);
        } else {
            anim.setPlayMode(Animation.PlayMode.NORMAL);
        }

        if (animation == null) setAnimation(anim);
        return anim;

    }

    public void setBoundaryRectangle() {
        float w = getWidth();
        float h = getHeight();
        float[] vertices = {0, 0, w, 0, w, h, 0, h};
        boundaryPolygon = new Polygon(vertices);
    }

    public void setBoundaryPolygon(int numSides) {
        float w = getWidth();
        float h = getHeight();
        float[] vertices = new float[2 * numSides];
        for(int i = 0; i < numSides; i++) {
            float angle = i * 6.28f / numSides;
            // x-coordinate
            vertices[2 * i] = w / 2 * MathUtils.cos(angle) + w / 2;
            // y-coordinate
            vertices[2 * i + 1] = h / 2 * MathUtils.sin(angle) + h / 2;
        }
        boundaryPolygon = new Polygon(vertices);
    }

    public  Polygon getBoundaryPolygon() {
        boundaryPolygon.setPosition(getX(), getY());
        boundaryPolygon.setOrigin(getOriginX(), getOriginY());
        boundaryPolygon.setRotation(getRotation());
        boundaryPolygon.setScale(getScaleX(), getScaleY());
        return boundaryPolygon;
    }

    public boolean overlaps(BaseActor other) {
        Polygon poly1 = this.getBoundaryPolygon();
        Polygon poly2 = other.getBoundaryPolygon();

        // initial test to improve performance
        if (!poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle())) {
            return false;
        }

        return Intersector.overlapConvexPolygons(poly1, poly2);
    }
    public Animation<TextureRegion> loadAnimationFromSheet(
            String filename, int rows, int cols,
            float frameDuration, boolean loop) {
            Texture texture = new Texture(Gdx.files.internal(filename), true);
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            int frameWidth = texture.getWidth() / cols;
            int frameHeight = texture.getHeight() / rows;

            TextureRegion[][] temp = TextureRegion.split(texture, frameWidth, frameHeight);
            Array<TextureRegion> textureRegionArray = new Array<>();
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    textureRegionArray.add(temp[r][c]);
                }
            }

            Animation<TextureRegion> anim = new Animation<>(frameDuration, textureRegionArray);
            if (loop) {
                anim.setPlayMode(Animation.PlayMode.LOOP);
            } else {
                anim.setPlayMode(Animation.PlayMode.NORMAL);
            }

            if(animation == null) setAnimation(anim);
            return anim;
    }

    public Animation<TextureRegion> loadTexture(String filename) {
        String[] filenames = new String[1];
        filenames[0] = filename;
        return loadAnimationFromFiles(filenames, 1, true);
    }

    public void setAnimation(Animation<TextureRegion> animation) {
        this.animation = animation;
        TextureRegion textureRegion = animation.getKeyFrame(0);
        float w = textureRegion.getRegionWidth();
        float h = textureRegion.getRegionHeight();
        setSize(w, h);
        setOrigin(w / 2, h / 2);

        if (boundaryPolygon == null)
            setBoundaryRectangle();
    }

    public void alignCamera() {
        Camera camera = this.getStage().getCamera();
        Viewport viewport = this.getStage().getViewport();

        camera.position.set(this.getX() + this.getOriginX(), this.getY() + this.getOriginY(), 0);

        camera.position.x = MathUtils.clamp(camera.position.x,
                camera.viewportWidth / 2 ,worldBounds.width - camera.viewportWidth / 2);

        camera.position.y = MathUtils.clamp(camera.position.y,
                camera.viewportHeight / 2, worldBounds.height - camera.viewportHeight / 2);
        camera.update();
    }

    public void setAnimationPaused(boolean animationPaused) {
        this.animationPaused = animationPaused;
    }

    public void setAcceleration(float acceleration) {
        this.acceleration = acceleration;
    }

    public void accelerationAtAngle(float angle) {
        accelerationVec.add(new Vector2(acceleration, 0)).setAngle(angle);
    }

    public void acelerationForward() {
        accelerationAtAngle(getRotation());
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (!animationPaused) {
            elapsedTime += delta;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        // apply coloe tint effect
        Color c = getColor();
        batch.setColor(c.r, c.g, c.b, c.a);

        if (animation != null && isVisible()) {
            batch.draw(animation.getKeyFrame(elapsedTime), getX(), getY(), getOriginX(),
                    getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        }
    }

    public boolean isAnimationFinished() {
        return animation.isAnimationFinished(elapsedTime);
    }

    public void setSpeed(float speed) {
        if (velocityVec.len() == 0) {
            velocityVec.set(speed, 0);
        } else {
            velocityVec.setLength(speed);
        }
    }

    public void applyPhysics(float deltaTime) {
        velocityVec.add(accelerationVec.x * deltaTime, accelerationVec.y * deltaTime);

        float speed = getSpeed();
        // decrease speed when not accelerating
        if (accelerationVec.len() == 0)
            speed -= deceleration * deltaTime;

        // keep speed within set bounds
        speed = MathUtils.clamp(speed, 0, maxSpeed);
        setSpeed(speed);

        moveBy(velocityVec.x * deltaTime, velocityVec.y * deltaTime);

        // reset acceleration
        accelerationVec.set(0, 0);
    }

    public float getSpeed() {
        return velocityVec.len();
    }

    public void setMotionAngle(float angle) {
        velocityVec.setAngle(angle);
    }

    public float getMotionAngle() {
        return velocityVec.angle();
    }

    public boolean isMoving() {
        return (getSpeed() > 0);
    }

    public void centerAtPosition(float x, float y) {
        setPosition(x - getWidth() / 2, y - getHeight() / 2);
    }

    public void centerAtActor(BaseActor other) {
        centerAtPosition(other.getX() + other.getWidth() / 2, other.getY() + other.getHeight() / 2);
    }

    public void setOpacity(float opacity) {
        this.getColor().a = opacity;
    }

    public Vector2 preventOverlap(BaseActor other) {
        Polygon poly1 = this.getBoundaryPolygon();
        Polygon poly2 = other.getBoundaryPolygon();

        // initial test to improve performance
        if (!poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle())) {
            return null;
        }

        Intersector.MinimumTranslationVector mtv = new Intersector.MinimumTranslationVector();
        boolean polygonOverlap = Intersector.overlapConvexPolygons(poly1, poly2, mtv);

        if (!polygonOverlap)
            return null;
        this.moveBy(mtv.normal.x * mtv.depth, mtv.normal.y * mtv.depth);
        return mtv.normal;
    }

    public static ArrayList<BaseActor> getList(Stage stage, String className) {
        ArrayList<BaseActor> list = new ArrayList<>();

        Class theClass = null;
        try {
            theClass = Class.forName(className);
        }catch (Exception error) {
            error.printStackTrace();
        }
        for(Actor actor: stage.getActors()) {
            if (theClass.isInstance(actor))
                list.add((BaseActor) actor);
        }
        return list;
    }

    public static int count(Stage stage, String className) {
        return getList(stage, className).size();
    }

    public static void setWorldBounds(float width, float height) {
        worldBounds = new Rectangle(0, 0, width, height);
    }

    public static void setWorldBounds(BaseActor ba) {
        setWorldBounds(ba.getWidth(), ba.getHeight());
    }

    public void boundToWorld() {
        if (getX() < 0)
            setX(0);

        if (getX() + getWidth() > worldBounds.width)
            setX(worldBounds.width - getWidth());

        if (getY() < 0)
            setY(0);

        if (getY() + getHeight() > worldBounds.height)
            setY(worldBounds.height - getHeight());
    }
}
