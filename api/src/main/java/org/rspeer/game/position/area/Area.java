package org.rspeer.game.position.area;

import java.util.List;
import org.rspeer.game.adapter.type.SceneNode;
import org.rspeer.game.position.Position;

public interface Area {

    static Area rectangular(Position a, Position b) {
        return new RectangularArea(a.getFloorLevel(), a, b);
    }

    static Area polygonal(Position... edges) {
        return new PolygonalArea(edges);
    }

    int getFloorLevel();

    List<Position> getTiles();

    boolean contains(SceneNode entity);
}