package org.o7.Fire.Glopion.Brain.Observation.Spatial;

import arc.struct.Seq;
import mindustry.Vars;
import mindustry.ctype.ContentType;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.world.Tile;

public class SpatialInformationExtractor {
    protected int registered = 0, index = 0;
    protected final Seq<Bullet> bulletSeq = new Seq<>();
    protected final Seq<Unit> unitSeq = new Seq<>();
    protected SpatialExtractor[] spatialExtractors = new SpatialExtractor[]{
    
    };
    //say its a thicc tensor
    public PortableSpatial[] portableSpatials = new PortableSpatial[]{
            new PortableSpatial<Unit>(360,0,Unit.class, Unit::rotation),
            new PortableSpatial<Unit>(1,0,Unit.class, Unit::healthf),
            new PortableSpatial<Unit>(1,-1,Unit.class, Unit::deltaX),
            new PortableSpatial<Unit>(1,-1,Unit.class, Unit::deltaY),
            new PortableSpatial<Unit>(1,-1,Unit.class, Unit::deltaLen),
            new PortableSpatial<Unit>(1,-1,Unit.class, Unit::deltaAngle),
            new PortableSpatial<Unit>(1,0,Unit.class, Unit::armor),
            new PortableSpatial<Unit>(1,0,Unit.class, Unit::elevation),
            new PortableSpatial<Unit>(1,0,Unit.class, Unit::hitSize),
            new PortableSpatial<Unit>(1,0,Unit.class, Unit::shield),
            new PortableSpatial<Unit>(Unit.class, Unit::disarmed),
            new PortableSpatial<Unit>(Unit.class, Unit::damaged),
            new PortableSpatial<Unit>(Unit.class, Unit::hovering),
            new PortableSpatial<Unit>(Unit.class, Unit::isShooting),
            new PortableSpatial<Unit>(Unit.class, Unit::isAI),
            new PortableSpatial<Unit>(Unit.class, Unit::isBuilding),
            new PortableSpatial<Unit>(Unit.class, Unit::isFlying),
            new PortableSpatial<Unit>(Unit.class, Unit::isPlayer),
            new PortableSpatial<Unit>(Unit.class, Unit::dead),
            new PortableSpatial<Unit>(Unit.class, Unit::isGrounded),
            new PortableSpatial<Unit>(Unit.class, Unit::isCommanding),
            new PortableSpatial<Unit>(Team.all.length,0,Unit.class, t->t.team().id),
            new PortableSpatial<Bullet>(360,0,Bullet.class, Bullet::rotation),
            new PortableSpatial<Bullet>(1,-1,Bullet.class, Bullet::damage),
            new PortableSpatial<Bullet>(1,0,Bullet.class, Bullet::damageMultiplier),
            new PortableSpatial<Bullet>(1,0,Bullet.class, Bullet::drag),
            new PortableSpatial<Bullet>(1,0,Bullet.class, Bullet::lifetime),
            new PortableSpatial<Bullet>(1,0,Bullet.class, Bullet::hitSize),
            new PortableSpatial<Bullet>(1,-1,Bullet.class, Bullet::deltaX),
            new PortableSpatial<Bullet>(1,-1,Bullet.class, Bullet::deltaY),
            new PortableSpatial<Bullet>(1,-1,Bullet.class, Bullet::deltaLen),
            new PortableSpatial<Bullet>(1,-1,Bullet.class, Bullet::deltaAngle),
            new PortableSpatial<Bullet>(Team.all.length,0,Bullet.class, t->t.team().id),
            new PortableSpatial<Tile>(1,0,Tile.class, Tile::getFlammability),
            new PortableSpatial<Tile>(Vars.content.getBy(ContentType.block).size,0,Tile.class, Tile::overlayID),
            new PortableSpatial<Tile>(Vars.content.getBy(ContentType.block).size,0,Tile.class, Tile::blockID),
            new PortableSpatial<Tile>(Vars.content.getBy(ContentType.block).size,0,Tile.class, Tile::floorID),
            new PortableSpatial<Tile>(Tile.class, Tile::dangerous),
            new PortableSpatial<Tile>(Team.all.length,0,Tile.class, t->t.getTeamID()),
            new PortableSpatial<Building>(1,0,Building.class, Building::healthf),
            new PortableSpatial<Building>(1,0,Building.class, Building::efficiency),
            new PortableSpatial<Building>(1,0,Building.class, Building::getPowerProduction),
            new PortableSpatial<Building>(1,0,Building.class, Building::hitSize),
            new PortableSpatial<Building>(360,0,Building.class, Building::rotdeg),
            new PortableSpatial<Building>(Building.class, Building::shouldConsume),
            new PortableSpatial<Building>(Building.class, Building::canUnload),
            new PortableSpatial<Building>(Building.class, Building::canPickup),
            new PortableSpatial<Building>(Building.class, Building::dead),
            new PortableSpatial<Building>(Building.class, Building::productionValid),
            new PortableSpatial<Building>(1,0,Building.class, b-> b.power() == null ? 0 : b.power().status),
            new PortableSpatial<Building>(1,0,Building.class, b-> b.power() == null ? 0 : b.power().graph.getLastCapacity() / b.power.graph.getLastPowerStored()),
            new PortableSpatial<Building>(1,0,Building.class, b-> b.power() == null ? 0 : b.power().graph.getPowerBalance()),
            new PortableSpatial<Building>(1,0,Building.class, b-> b.power() == null ? 0 : b.power().graph.getSatisfaction()),
            new PortableSpatial<Building>(1,0,Building.class, b-> b.power() == null ? 0 : b.power().graph.getUsageFraction()),
    };
    public float[] getLow(){
        float[] low = new float[size()];
        reset();
        for (PortableSpatial portableSpatial : portableSpatials) {
            low[index] = portableSpatial.getLow();
            index++;
        }
        for(SpatialExtractor spatialExtractor : spatialExtractors) {
            for (int i = 0; i < spatialExtractor.size(); i++) {
                low[index] = spatialExtractor.low(i);
            }
            index++;
        }
        return low;
    }
    public float[] getHigh(){
        float[] high = new float[size()];
        reset();
        for (PortableSpatial portableSpatial : portableSpatials) {
            high[index] = portableSpatial.getHigh();
            index++;
        }
        for(SpatialExtractor spatialExtractor : spatialExtractors) {
            for (int i = 0; i < spatialExtractor.size(); i++) {
                high[index] = spatialExtractor.high(i);
            }
            index++;
        }
        return high;
    }
    public SpatialInformationExtractor(){
        registered = portableSpatials.length;
        for(SpatialExtractor spatialExtractor : spatialExtractors)
            registered = registered + spatialExtractor.size();
        
    }
    
    protected void reset(){
        bulletSeq.clear();
        unitSeq.clear();
        index = 0;
    }
    
    public void read(float x, float y, float[] floats){
        reset();
        Groups.bullet.intersect(x,y, 4,4, bulletSeq::add);
        Groups.unit.intersect(x,y, 1,1, unitSeq::add);
        //
        Tile tile = Vars.world.tileWorld(x,y);
        Bullet bullet = null;
        Unit unit = null;
        Building building = null;
       // Player player = null;
        //
        if(tile != null)
            building = tile.build;
        if(bulletSeq.any())
            bullet = bulletSeq.first();
        if(unitSeq.any())
            unit = unitSeq.first();
        //if(unit != null)
        //    player = unit.getPlayer();
        for(PortableSpatial p : portableSpatials){
            //// if (Player.class == p.tClass){
              //  floats[index] = p.process(player);
            //}else
                if(Building.class == p.tClass){
                floats[index] =  p.process(building);
            }else if(Unit.class == p.tClass){
                floats[index] =  p.process(unit);
            }else if(Bullet.class == p.tClass){
                floats[index] =  p.process(bullet);
            }else if(Tile.class == p.tClass){
                floats[index] =  p.process(tile);
            }
            index++;
        }
        for (int i = 0, spatialExtractorsLength = spatialExtractors.length; i < spatialExtractorsLength; i++) {
            SpatialExtractor spatialExtractor = spatialExtractors[i];
            spatialExtractor.set(this, floats);
            spatialExtractor.process(tile);
            spatialExtractor.process(building);
            spatialExtractor.process(bullet);
            spatialExtractor.process(unit);
            //spatialExtractor.process(player);
        }
    }
    
    public int size(){
        return registered;
    }
}


