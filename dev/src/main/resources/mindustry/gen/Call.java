//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package mindustry.gen;

import arc.graphics.Color;
import mindustry.Vars;
import mindustry.ai.WaveSpawner;
import mindustry.core.Logic;
import mindustry.core.NetClient;
import mindustry.core.NetServer;
import mindustry.ctype.Content;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Rules;
import mindustry.game.Team;
import mindustry.input.InputHandler;
import mindustry.net.Net;
import mindustry.net.NetConnection;
import mindustry.net.Administration.TraceInfo;
import mindustry.net.Packets.AdminAction;
import mindustry.net.Packets.KickReason;
import mindustry.type.Item;
import mindustry.type.UnitType;
import mindustry.type.Weather;
import mindustry.ui.fragments.HudFragment;
import mindustry.world.Block;
import mindustry.world.Build;
import mindustry.world.Tile;
import mindustry.world.blocks.ConstructBlock;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.blocks.units.UnitBlock;

public class Call {
    public Call() {
    }
    
    public static void adminRequest(Player other, AdminAction action) {
        if (Vars.net.server() || !Vars.net.active()) {
            NetServer.adminRequest(Vars.player, other, action);
        }
        
        if (Vars.net.client()) {
            AdminRequestCallPacket packet = new AdminRequestCallPacket();
            packet.other = other;
            packet.action = action;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void announce(String message) {
        if (Vars.net.server()) {
            AnnounceCallPacket packet = new AnnounceCallPacket();
            packet.message = message;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void announce(NetConnection playerConnection, String message) {
        if (Vars.net.server()) {
            AnnounceCallPacket packet = new AnnounceCallPacket();
            packet.message = message;
            playerConnection.send(packet, true);
        }
        
    }
    
    public static void beginBreak(Unit unit, Team team, int x, int y) {
        if (Vars.net.server() || !Vars.net.active()) {
            Build.beginBreak(unit, team, x, y);
        }
        
        if (Vars.net.server()) {
            BeginBreakCallPacket packet = new BeginBreakCallPacket();
            packet.unit = unit;
            packet.team = team;
            packet.x = x;
            packet.y = y;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void beginPlace(Unit unit, Block result, Team team, int x, int y, int rotation) {
        if (Vars.net.server() || !Vars.net.active()) {
            Build.beginPlace(unit, result, team, x, y, rotation);
        }
        
        if (Vars.net.server()) {
            BeginPlaceCallPacket packet = new BeginPlaceCallPacket();
            packet.unit = unit;
            packet.result = result;
            packet.team = team;
            packet.x = x;
            packet.y = y;
            packet.rotation = rotation;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void blockSnapshot(short amount, byte[] data) {
        if (Vars.net.server()) {
            BlockSnapshotCallPacket packet = new BlockSnapshotCallPacket();
            packet.amount = amount;
            packet.data = data;
            Vars.net.send(packet, false);
        }
        
    }
    
    public static void blockSnapshot(NetConnection playerConnection, short amount, byte[] data) {
        if (Vars.net.server()) {
            BlockSnapshotCallPacket packet = new BlockSnapshotCallPacket();
            packet.amount = amount;
            packet.data = data;
            playerConnection.send(packet, false);
        }
        
    }
    
    public static void buildingControlSelect(Player player, Building build) {
        InputHandler.buildingControlSelect(player, build);
        if (Vars.net.server() || Vars.net.client()) {
            BuildingControlSelectCallPacket packet = new BuildingControlSelectCallPacket();
            if (Vars.net.server()) {
                packet.player = player;
            }
            
            packet.build = build;
            Vars.net.send(packet, true);
        }
        
    }
    
    static void buildingControlSelect__forward(NetConnection exceptConnection, Player player, Building build) {
        if (Vars.net.server() || Vars.net.client()) {
            BuildingControlSelectCallPacket packet = new BuildingControlSelectCallPacket();
            if (Vars.net.server()) {
                packet.player = player;
            }
            
            packet.build = build;
            Vars.net.sendExcept(exceptConnection, packet, true);
        }
        
    }
    
    public static void clearItems(Building build) {
        if (Vars.net.server() || !Vars.net.active()) {
            InputHandler.clearItems(build);
        }
        
        if (Vars.net.server()) {
            ClearItemsCallPacket packet = new ClearItemsCallPacket();
            packet.build = build;
            Vars.net.send(packet, false);
        }
        
    }
    
    public static void clientPacketReliable(String type, String contents) {
        if (Vars.net.server()) {
            ClientPacketReliableCallPacket packet = new ClientPacketReliableCallPacket();
            packet.type = type;
            packet.contents = contents;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void clientPacketReliable(NetConnection playerConnection, String type, String contents) {
        if (Vars.net.server()) {
            ClientPacketReliableCallPacket packet = new ClientPacketReliableCallPacket();
            packet.type = type;
            packet.contents = contents;
            playerConnection.send(packet, true);
        }
        
    }
    
    public static void clientPacketUnreliable(String type, String contents) {
        if (Vars.net.server()) {
            ClientPacketUnreliableCallPacket packet = new ClientPacketUnreliableCallPacket();
            packet.type = type;
            packet.contents = contents;
            Vars.net.send(packet, false);
        }
        
    }
    
    public static void clientPacketUnreliable(NetConnection playerConnection, String type, String contents) {
        if (Vars.net.server()) {
            ClientPacketUnreliableCallPacket packet = new ClientPacketUnreliableCallPacket();
            packet.type = type;
            packet.contents = contents;
            playerConnection.send(packet, false);
        }
        
    }
    
    public static void clientSnapshot(int snapshotID, int unitID, boolean dead, float x, float y, float pointerX, float pointerY, float rotation, float baseRotation, float xVelocity, float yVelocity, Tile mining, boolean boosting, boolean shooting, boolean chatting, boolean building, BuildPlan[] requests, float viewX, float viewY, float viewWidth, float viewHeight) {
        if (Vars.net.client()) {
            ClientSnapshotCallPacket packet = new ClientSnapshotCallPacket();
            packet.snapshotID = snapshotID;
            packet.unitID = unitID;
            packet.dead = dead;
            packet.x = x;
            packet.y = y;
            packet.pointerX = pointerX;
            packet.pointerY = pointerY;
            packet.rotation = rotation;
            packet.baseRotation = baseRotation;
            packet.xVelocity = xVelocity;
            packet.yVelocity = yVelocity;
            packet.mining = mining;
            packet.boosting = boosting;
            packet.shooting = shooting;
            packet.chatting = chatting;
            packet.building = building;
            packet.requests = requests;
            packet.viewX = viewX;
            packet.viewY = viewY;
            packet.viewWidth = viewWidth;
            packet.viewHeight = viewHeight;
            Vars.net.send(packet, false);
        }
        
    }
    
    public static void connect(NetConnection playerConnection, String ip, int port) {
        if (Vars.net.client() || !Vars.net.active()) {
            NetClient.connect(ip, port);
        }
        
        if (Vars.net.server()) {
            ConnectCallPacket packet = new ConnectCallPacket();
            packet.ip = ip;
            packet.port = port;
            playerConnection.send(packet, true);
        }
        
    }
    
    public static void connectConfirm() {
        if (Vars.net.client()) {
            ConnectConfirmCallPacket packet = new ConnectConfirmCallPacket();
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void constructFinish(Tile tile, Block block, Unit builder, byte rotation, Team team, Object config) {
        if (Vars.net.server() || !Vars.net.active()) {
            ConstructBlock.constructFinish(tile, block, builder, rotation, team, config);
        }
        
        if (Vars.net.server()) {
            ConstructFinishCallPacket packet = new ConstructFinishCallPacket();
            packet.tile = tile;
            packet.block = block;
            packet.builder = builder;
            packet.rotation = rotation;
            packet.team = team;
            packet.config = config;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void createBullet(BulletType type, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl) {
        if (Vars.net.server() || !Vars.net.active()) {
            BulletType.createBullet(type, team, x, y, angle, damage, velocityScl, lifetimeScl);
        }
        
        if (Vars.net.server()) {
            CreateBulletCallPacket packet = new CreateBulletCallPacket();
            packet.type = type;
            packet.team = team;
            packet.x = x;
            packet.y = y;
            packet.angle = angle;
            packet.damage = damage;
            packet.velocityScl = velocityScl;
            packet.lifetimeScl = lifetimeScl;
            Vars.net.send(packet, false);
        }
        
    }
    
    public static void createWeather(Weather weather, float intensity, float duration, float windX, float windY) {
        if (Vars.net.server() || !Vars.net.active()) {
            Weather.createWeather(weather, intensity, duration, windX, windY);
        }
        
        if (Vars.net.server()) {
            CreateWeatherCallPacket packet = new CreateWeatherCallPacket();
            packet.weather = weather;
            packet.intensity = intensity;
            packet.duration = duration;
            packet.windX = windX;
            packet.windY = windY;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void deconstructFinish(Tile tile, Block block, Unit builder) {
        if (Vars.net.server() || !Vars.net.active()) {
            ConstructBlock.deconstructFinish(tile, block, builder);
        }
        
        if (Vars.net.server()) {
            DeconstructFinishCallPacket packet = new DeconstructFinishCallPacket();
            packet.tile = tile;
            packet.block = block;
            packet.builder = builder;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void deletePlans(Player player, int[] positions) {
        InputHandler.deletePlans(player, positions);
        if (Vars.net.server() || Vars.net.client()) {
            DeletePlansCallPacket packet = new DeletePlansCallPacket();
            if (Vars.net.server()) {
                packet.player = player;
            }
            
            packet.positions = positions;
            Vars.net.send(packet, false);
        }
        
    }
    
    static void deletePlans__forward(NetConnection exceptConnection, Player player, int[] positions) {
        if (Vars.net.server() || Vars.net.client()) {
            DeletePlansCallPacket packet = new DeletePlansCallPacket();
            if (Vars.net.server()) {
                packet.player = player;
            }
            
            packet.positions = positions;
            Vars.net.sendExcept(exceptConnection, packet, false);
        }
        
    }
    
    public static void dropItem(float angle) {
        if (Vars.net.server() || !Vars.net.active()) {
            InputHandler.dropItem(Vars.player, angle);
        }
        
        if (Vars.net.client()) {
            DropItemCallPacket packet = new DropItemCallPacket();
            packet.angle = angle;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void effect(Effect effect, float x, float y, float rotation, Color color) {
        if (Vars.net.server()) {
            EffectCallPacket packet = new EffectCallPacket();
            packet.effect = effect;
            packet.x = x;
            packet.y = y;
            packet.rotation = rotation;
            packet.color = color;
            Vars.net.send(packet, false);
        }
        
    }
    
    public static void effect(NetConnection playerConnection, Effect effect, float x, float y, float rotation, Color color) {
        if (Vars.net.server()) {
            EffectCallPacket packet = new EffectCallPacket();
            packet.effect = effect;
            packet.x = x;
            packet.y = y;
            packet.rotation = rotation;
            packet.color = color;
            playerConnection.send(packet, false);
        }
        
    }
    
    public static void effectReliable(Effect effect, float x, float y, float rotation, Color color) {
        if (Vars.net.server()) {
            EffectReliableCallPacket packet = new EffectReliableCallPacket();
            packet.effect = effect;
            packet.x = x;
            packet.y = y;
            packet.rotation = rotation;
            packet.color = color;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void effectReliable(NetConnection playerConnection, Effect effect, float x, float y, float rotation, Color color) {
        if (Vars.net.server()) {
            EffectReliableCallPacket packet = new EffectReliableCallPacket();
            packet.effect = effect;
            packet.x = x;
            packet.y = y;
            packet.rotation = rotation;
            packet.color = color;
            playerConnection.send(packet, true);
        }
        
    }
    
    public static void entitySnapshot(NetConnection playerConnection, short amount, byte[] data) {
        if (Vars.net.server()) {
            EntitySnapshotCallPacket packet = new EntitySnapshotCallPacket();
            packet.amount = amount;
            packet.data = data;
            playerConnection.send(packet, false);
        }
        
    }
    
    public static void gameOver(Team winner) {
        Logic.gameOver(winner);
        if (Vars.net.server()) {
            GameOverCallPacket packet = new GameOverCallPacket();
            packet.winner = winner;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void hideHudText() {
        if (Vars.net.server()) {
            HideHudTextCallPacket packet = new HideHudTextCallPacket();
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void hideHudText(NetConnection playerConnection) {
        if (Vars.net.server()) {
            HideHudTextCallPacket packet = new HideHudTextCallPacket();
            playerConnection.send(packet, true);
        }
        
    }
    
    public static void infoMessage(String message) {
        if (Vars.net.server()) {
            InfoMessageCallPacket packet = new InfoMessageCallPacket();
            packet.message = message;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void infoMessage(NetConnection playerConnection, String message) {
        if (Vars.net.server()) {
            InfoMessageCallPacket packet = new InfoMessageCallPacket();
            packet.message = message;
            playerConnection.send(packet, true);
        }
        
    }
    
    public static void infoPopup(String message, float duration, int align, int top, int left, int bottom, int right) {
        if (Vars.net.server()) {
            InfoPopupCallPacket packet = new InfoPopupCallPacket();
            packet.message = message;
            packet.duration = duration;
            packet.align = align;
            packet.top = top;
            packet.left = left;
            packet.bottom = bottom;
            packet.right = right;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void infoPopup(NetConnection playerConnection, String message, float duration, int align, int top, int left, int bottom, int right) {
        if (Vars.net.server()) {
            InfoPopupCallPacket packet = new InfoPopupCallPacket();
            packet.message = message;
            packet.duration = duration;
            packet.align = align;
            packet.top = top;
            packet.left = left;
            packet.bottom = bottom;
            packet.right = right;
            playerConnection.send(packet, true);
        }
        
    }
    
    public static void infoToast(String message, float duration) {
        if (Vars.net.server()) {
            InfoToastCallPacket packet = new InfoToastCallPacket();
            packet.message = message;
            packet.duration = duration;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void infoToast(NetConnection playerConnection, String message, float duration) {
        if (Vars.net.server()) {
            InfoToastCallPacket packet = new InfoToastCallPacket();
            packet.message = message;
            packet.duration = duration;
            playerConnection.send(packet, true);
        }
        
    }
    
    public static void kick(NetConnection playerConnection, String reason) {
        if (Vars.net.server()) {
            KickCallPacket packet = new KickCallPacket();
            packet.reason = reason;
            playerConnection.send(packet, true);
        }
        
    }
    
    public static void kick(NetConnection playerConnection, KickReason reason) {
        if (Vars.net.server()) {
            KickCallPacket2 packet = new KickCallPacket2();
            packet.reason = reason;
            playerConnection.send(packet, true);
        }
        
    }
    
    public static void label(String message, float duration, float worldx, float worldy) {
        if (Vars.net.server()) {
            LabelCallPacket packet = new LabelCallPacket();
            packet.message = message;
            packet.duration = duration;
            packet.worldx = worldx;
            packet.worldy = worldy;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void label(NetConnection playerConnection, String message, float duration, float worldx, float worldy) {
        if (Vars.net.server()) {
            LabelCallPacket packet = new LabelCallPacket();
            packet.message = message;
            packet.duration = duration;
            packet.worldx = worldx;
            packet.worldy = worldy;
            playerConnection.send(packet, true);
        }
        
    }
    
    public static void payloadDropped(Unit unit, float x, float y) {
        if (Vars.net.server() || !Vars.net.active()) {
            InputHandler.payloadDropped(unit, x, y);
        }
        
        if (Vars.net.server()) {
            PayloadDroppedCallPacket packet = new PayloadDroppedCallPacket();
            packet.unit = unit;
            packet.x = x;
            packet.y = y;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void pickedBuildPayload(Unit unit, Building build, boolean onGround) {
        if (Vars.net.server() || !Vars.net.active()) {
            InputHandler.pickedBuildPayload(unit, build, onGround);
        }
        
        if (Vars.net.server()) {
            PickedBuildPayloadCallPacket packet = new PickedBuildPayloadCallPacket();
            packet.unit = unit;
            packet.build = build;
            packet.onGround = onGround;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void pickedUnitPayload(Unit unit, Unit target) {
        if (Vars.net.server() || !Vars.net.active()) {
            InputHandler.pickedUnitPayload(unit, target);
        }
        
        if (Vars.net.server()) {
            PickedUnitPayloadCallPacket packet = new PickedUnitPayloadCallPacket();
            packet.unit = unit;
            packet.target = target;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void ping(long time) {
        if (Vars.net.client()) {
            PingCallPacket packet = new PingCallPacket();
            packet.time = time;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void pingResponse(NetConnection playerConnection, long time) {
        if (Vars.net.server()) {
            PingResponseCallPacket packet = new PingResponseCallPacket();
            packet.time = time;
            playerConnection.send(packet, true);
        }
        
    }
    
    public static void playerDisconnect(int playerid) {
        if (Vars.net.server()) {
            PlayerDisconnectCallPacket packet = new PlayerDisconnectCallPacket();
            packet.playerid = playerid;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void playerSpawn(Tile tile, Player player) {
        if (Vars.net.server() || !Vars.net.active()) {
            CoreBlock.playerSpawn(tile, player);
        }
        
        if (Vars.net.server()) {
            PlayerSpawnCallPacket packet = new PlayerSpawnCallPacket();
            packet.tile = tile;
            packet.player = player;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void removeQueueBlock(NetConnection playerConnection, int x, int y, boolean breaking) {
        if (Vars.net.server()) {
            RemoveQueueBlockCallPacket packet = new RemoveQueueBlockCallPacket();
            packet.x = x;
            packet.y = y;
            packet.breaking = breaking;
            playerConnection.send(packet, true);
        }
        
    }
    
    public static void removeTile(Tile tile) {
        if (Vars.net.server() || !Vars.net.active()) {
            Tile.removeTile(tile);
        }
        
        if (Vars.net.server()) {
            RemoveTileCallPacket packet = new RemoveTileCallPacket();
            packet.tile = tile;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void requestBuildPayload(Player player, Building build) {
        if (Vars.net.server() || !Vars.net.active()) {
            InputHandler.requestBuildPayload(player, build);
        }
        
        if (Vars.net.server() || Vars.net.client()) {
            RequestBuildPayloadCallPacket packet = new RequestBuildPayloadCallPacket();
            if (Vars.net.server()) {
                packet.player = player;
            }
            
            packet.build = build;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void requestDropPayload(Player player, float x, float y) {
        if (Vars.net.server() || !Vars.net.active()) {
            InputHandler.requestDropPayload(player, x, y);
        }
        
        if (Vars.net.server() || Vars.net.client()) {
            RequestDropPayloadCallPacket packet = new RequestDropPayloadCallPacket();
            if (Vars.net.server()) {
                packet.player = player;
            }
            
            packet.x = x;
            packet.y = y;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void requestItem(Player player, Building build, Item item, int amount) {
        if (Vars.net.server() || !Vars.net.active()) {
            InputHandler.requestItem(player, build, item, amount);
        }
        
        if (Vars.net.server() || Vars.net.client()) {
            RequestItemCallPacket packet = new RequestItemCallPacket();
            if (Vars.net.server()) {
                packet.player = player;
            }
            
            packet.build = build;
            packet.item = item;
            packet.amount = amount;
            Vars.net.send(packet, true);
        }
        
    }
    
    static void requestItem__forward(NetConnection exceptConnection, Player player, Building build, Item item, int amount) {
        if (Vars.net.server() || Vars.net.client()) {
            RequestItemCallPacket packet = new RequestItemCallPacket();
            if (Vars.net.server()) {
                packet.player = player;
            }
            
            packet.build = build;
            packet.item = item;
            packet.amount = amount;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void requestUnitPayload(Player player, Unit target) {
        if (Vars.net.server() || !Vars.net.active()) {
            InputHandler.requestUnitPayload(player, target);
        }
        
        if (Vars.net.server() || Vars.net.client()) {
            RequestUnitPayloadCallPacket packet = new RequestUnitPayloadCallPacket();
            if (Vars.net.server()) {
                packet.player = player;
            }
            
            packet.target = target;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void researched(Content content) {
        if (Vars.net.server()) {
            ResearchedCallPacket packet = new ResearchedCallPacket();
            packet.content = content;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void rotateBlock(Player player, Building build, boolean direction) {
        if (Vars.net.server() || !Vars.net.active()) {
            InputHandler.rotateBlock(player, build, direction);
        }
        
        if (Vars.net.server() || Vars.net.client()) {
            RotateBlockCallPacket packet = new RotateBlockCallPacket();
            if (Vars.net.server()) {
                packet.player = player;
            }
            
            packet.build = build;
            packet.direction = direction;
            Vars.net.send(packet, false);
        }
        
    }
    
    static void rotateBlock__forward(NetConnection exceptConnection, Player player, Building build, boolean direction) {
        if (Vars.net.server() || Vars.net.client()) {
            RotateBlockCallPacket packet = new RotateBlockCallPacket();
            if (Vars.net.server()) {
                packet.player = player;
            }
            
            packet.build = build;
            packet.direction = direction;
            Vars.net.send(packet, false);
        }
        
    }
    
    public static void sectorCapture() {
        if (Vars.net.server() || !Vars.net.active()) {
            Logic.sectorCapture();
        }
        
        if (Vars.net.server()) {
            SectorCaptureCallPacket packet = new SectorCaptureCallPacket();
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void sectorProduced(int[] amounts) {
        if (Vars.net.server()) {
            SectorProducedCallPacket packet = new SectorProducedCallPacket();
            packet.amounts = amounts;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void sendChatMessage(String message) {
        if (Vars.net.server() || !Vars.net.active()) {
            NetClient.sendChatMessage(Vars.player, message);
        }
        
        if (Vars.net.client()) {
            SendChatMessageCallPacket packet = new SendChatMessageCallPacket();
            packet.message = message;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void sendMessage(String message) {
        if (Vars.net.server() || !Vars.net.active()) {
            NetClient.sendMessage(message);
        }
        
        if (Vars.net.server()) {
            SendMessageCallPacket packet = new SendMessageCallPacket();
            packet.message = message;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void sendMessage(String message, String sender, Player playersender) {
        if (Vars.net.server()) {
            SendMessageCallPacket2 packet = new SendMessageCallPacket2();
            packet.message = message;
            packet.sender = sender;
            packet.playersender = playersender;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void sendMessage(NetConnection playerConnection, String message, String sender, Player playersender) {
        if (Vars.net.server()) {
            SendMessageCallPacket2 packet = new SendMessageCallPacket2();
            packet.message = message;
            packet.sender = sender;
            packet.playersender = playersender;
            playerConnection.send(packet, true);
        }
        
    }
    
    public static void serverPacketReliable(String type, String contents) {
        if (Vars.net.client()) {
            ServerPacketReliableCallPacket packet = new ServerPacketReliableCallPacket();
            packet.type = type;
            packet.contents = contents;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void serverPacketUnreliable(String type, String contents) {
        if (Vars.net.client()) {
            ServerPacketUnreliableCallPacket packet = new ServerPacketUnreliableCallPacket();
            packet.type = type;
            packet.contents = contents;
            Vars.net.send(packet, false);
        }
        
    }
    
    public static void setFloor(Tile tile, Block floor, Block overlay) {
        if (Vars.net.server() || !Vars.net.active()) {
            Tile.setFloor(tile, floor, overlay);
        }
        
        if (Vars.net.server()) {
            SetFloorCallPacket packet = new SetFloorCallPacket();
            packet.tile = tile;
            packet.floor = floor;
            packet.overlay = overlay;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void setHudText(String message) {
        if (Vars.net.server()) {
            SetHudTextCallPacket packet = new SetHudTextCallPacket();
            packet.message = message;
            Vars.net.send(packet, false);
        }
        
    }
    
    public static void setHudText(NetConnection playerConnection, String message) {
        if (Vars.net.server()) {
            SetHudTextCallPacket packet = new SetHudTextCallPacket();
            packet.message = message;
            playerConnection.send(packet, false);
        }
        
    }
    
    public static void setHudTextReliable(String message) {
        if (Vars.net.server()) {
            SetHudTextReliableCallPacket packet = new SetHudTextReliableCallPacket();
            packet.message = message;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void setHudTextReliable(NetConnection playerConnection, String message) {
        if (Vars.net.server()) {
            SetHudTextReliableCallPacket packet = new SetHudTextReliableCallPacket();
            packet.message = message;
            playerConnection.send(packet, true);
        }
        
    }
    
    public static void setItem(Building build, Item item, int amount) {
        if (Vars.net.server() || !Vars.net.active()) {
            InputHandler.setItem(build, item, amount);
        }
        
        if (Vars.net.server()) {
            SetItemCallPacket packet = new SetItemCallPacket();
            packet.build = build;
            packet.item = item;
            packet.amount = amount;
            Vars.net.send(packet, false);
        }
        
    }
    
    public static void setPlayerTeamEditor(Player player, Team team) {
        HudFragment.setPlayerTeamEditor(player, team);
        if (Vars.net.server() || Vars.net.client()) {
            SetPlayerTeamEditorCallPacket packet = new SetPlayerTeamEditorCallPacket();
            if (Vars.net.server()) {
                packet.player = player;
            }
            
            packet.team = team;
            Vars.net.send(packet, true);
        }
        
    }
    
    static void setPlayerTeamEditor__forward(NetConnection exceptConnection, Player player, Team team) {
        if (Vars.net.server() || Vars.net.client()) {
            SetPlayerTeamEditorCallPacket packet = new SetPlayerTeamEditorCallPacket();
            if (Vars.net.server()) {
                packet.player = player;
            }
            
            packet.team = team;
            Vars.net.sendExcept(exceptConnection, packet, true);
        }
        
    }
    
    public static void setPosition(NetConnection playerConnection, float x, float y) {
        if (Vars.net.server()) {
            SetPositionCallPacket packet = new SetPositionCallPacket();
            packet.x = x;
            packet.y = y;
            playerConnection.send(packet, true);
        }
        
    }
    
    public static void setRules(Rules rules) {
        if (Vars.net.server()) {
            SetRulesCallPacket packet = new SetRulesCallPacket();
            packet.rules = rules;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void setRules(NetConnection playerConnection, Rules rules) {
        if (Vars.net.server()) {
            SetRulesCallPacket packet = new SetRulesCallPacket();
            packet.rules = rules;
            playerConnection.send(packet, true);
        }
        
    }
    
    public static void setTeam(Building build, Team team) {
        if (Vars.net.server() || !Vars.net.active()) {
            Tile.setTeam(build, team);
        }
        
        if (Vars.net.server()) {
            SetTeamCallPacket packet = new SetTeamCallPacket();
            packet.build = build;
            packet.team = team;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void setTile(Tile tile, Block block, Team team, int rotation) {
        if (Vars.net.server() || !Vars.net.active()) {
            Tile.setTile(tile, block, team, rotation);
        }
        
        if (Vars.net.server()) {
            SetTileCallPacket packet = new SetTileCallPacket();
            packet.tile = tile;
            packet.block = block;
            packet.team = team;
            packet.rotation = rotation;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void spawnEffect(float x, float y, float rotation, UnitType u) {
        if (Vars.net.server() || !Vars.net.active()) {
            WaveSpawner.spawnEffect(x, y, rotation, u);
        }
        
        if (Vars.net.server()) {
            SpawnEffectCallPacket packet = new SpawnEffectCallPacket();
            packet.x = x;
            packet.y = y;
            packet.rotation = rotation;
            packet.u = u;
            Vars.net.send(packet, false);
        }
        
    }
    
    public static void stateSnapshot(NetConnection playerConnection, float waveTime, int wave, int enemies, boolean paused, boolean gameOver, int timeData, byte tps, byte[] coreData) {
        if (Vars.net.server()) {
            StateSnapshotCallPacket packet = new StateSnapshotCallPacket();
            packet.waveTime = waveTime;
            packet.wave = wave;
            packet.enemies = enemies;
            packet.paused = paused;
            packet.gameOver = gameOver;
            packet.timeData = timeData;
            packet.tps = tps;
            packet.coreData = coreData;
            playerConnection.send(packet, false);
        }
        
    }
    
    public static void takeItems(Building build, Item item, int amount, Unit to) {
        if (Vars.net.server() || !Vars.net.active()) {
            InputHandler.takeItems(build, item, amount, to);
        }
        
        if (Vars.net.server()) {
            TakeItemsCallPacket packet = new TakeItemsCallPacket();
            packet.build = build;
            packet.item = item;
            packet.amount = amount;
            packet.to = to;
            Vars.net.send(packet, false);
        }
        
    }
    
    public static void tileConfig(Player player, Building build, Object value) {
        InputHandler.tileConfig(player, build, value);
        if (Vars.net.server() || Vars.net.client()) {
            TileConfigCallPacket packet = new TileConfigCallPacket();
            if (Vars.net.server()) {
                packet.player = player;
            }
            
            packet.build = build;
            packet.value = value;
            Vars.net.send(packet, true);
        }
        
    }
    
    static void tileConfig__forward(NetConnection exceptConnection, Player player, Building build, Object value) {
        if (Vars.net.server() || Vars.net.client()) {
            TileConfigCallPacket packet = new TileConfigCallPacket();
            if (Vars.net.server()) {
                packet.player = player;
            }
            
            packet.build = build;
            packet.value = value;
            Vars.net.sendExcept(exceptConnection, packet, true);
        }
        
    }
    
    public static void tileDamage(Building build, float health) {
        if (Vars.net.server() || !Vars.net.active()) {
            Tile.tileDamage(build, health);
        }
        
        if (Vars.net.server()) {
            TileDamageCallPacket packet = new TileDamageCallPacket();
            packet.build = build;
            packet.health = health;
            Vars.net.send(packet, false);
        }
        
    }
    
    public static void tileDestroyed(Building build) {
        if (Vars.net.server() || !Vars.net.active()) {
            Tile.tileDestroyed(build);
        }
        
        if (Vars.net.server()) {
            TileDestroyedCallPacket packet = new TileDestroyedCallPacket();
            packet.build = build;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void tileTap(Player player, Tile tile) {
        InputHandler.tileTap(player, tile);
        if (Vars.net.server() || Vars.net.client()) {
            TileTapCallPacket packet = new TileTapCallPacket();
            if (Vars.net.server()) {
                packet.player = player;
            }
            
            packet.tile = tile;
            Vars.net.send(packet, false);
        }
        
    }
    
    public static void traceInfo(NetConnection playerConnection, Player player, TraceInfo info) {
        if (Vars.net.server()) {
            TraceInfoCallPacket packet = new TraceInfoCallPacket();
            packet.player = player;
            packet.info = info;
            playerConnection.send(packet, true);
        }
        
    }
    
    public static void transferInventory(Player player, Building build) {
        if (Vars.net.server() || !Vars.net.active()) {
            InputHandler.transferInventory(player, build);
        }
        
        if (Vars.net.server() || Vars.net.client()) {
            TransferInventoryCallPacket packet = new TransferInventoryCallPacket();
            if (Vars.net.server()) {
                packet.player = player;
            }
            
            packet.build = build;
            Vars.net.send(packet, true);
        }
        
    }
    
    static void transferInventory__forward(NetConnection exceptConnection, Player player, Building build) {
        if (Vars.net.server() || Vars.net.client()) {
            TransferInventoryCallPacket packet = new TransferInventoryCallPacket();
            if (Vars.net.server()) {
                packet.player = player;
            }
            
            packet.build = build;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void transferItemEffect(Item item, float x, float y, Itemsc to) {
        if (Vars.net.server() || !Vars.net.active()) {
            InputHandler.transferItemEffect(item, x, y, to);
        }
        
        if (Vars.net.server()) {
            TransferItemEffectCallPacket packet = new TransferItemEffectCallPacket();
            packet.item = item;
            packet.x = x;
            packet.y = y;
            packet.to = to;
            Vars.net.send(packet, false);
        }
        
    }
    
    public static void transferItemTo(Unit unit, Item item, int amount, float x, float y, Building build) {
        if (Vars.net.server() || !Vars.net.active()) {
            InputHandler.transferItemTo(unit, item, amount, x, y, build);
        }
        
        if (Vars.net.server()) {
            TransferItemToCallPacket packet = new TransferItemToCallPacket();
            packet.unit = unit;
            packet.item = item;
            packet.amount = amount;
            packet.x = x;
            packet.y = y;
            packet.build = build;
            Vars.net.send(packet, false);
        }
        
    }
    
    public static void transferItemToUnit(Item item, float x, float y, Itemsc to) {
        if (Vars.net.server() || !Vars.net.active()) {
            InputHandler.transferItemToUnit(item, x, y, to);
        }
        
        if (Vars.net.server()) {
            TransferItemToUnitCallPacket packet = new TransferItemToUnitCallPacket();
            packet.item = item;
            packet.x = x;
            packet.y = y;
            packet.to = to;
            Vars.net.send(packet, false);
        }
        
    }
    
    public static void unitBlockSpawn(Tile tile) {
        if (Vars.net.server() || !Vars.net.active()) {
            UnitBlock.unitBlockSpawn(tile);
        }
        
        if (Vars.net.server()) {
            UnitBlockSpawnCallPacket packet = new UnitBlockSpawnCallPacket();
            packet.tile = tile;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void unitCapDeath(Unit unit) {
        if (Vars.net.server() || !Vars.net.active()) {
            Units.unitCapDeath(unit);
        }
        
        if (Vars.net.server()) {
            UnitCapDeathCallPacket packet = new UnitCapDeathCallPacket();
            packet.unit = unit;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void unitClear(Player player) {
        if (Vars.net.server() || !Vars.net.active()) {
            InputHandler.unitClear(player);
        }
        
        if (Vars.net.server() || Vars.net.client()) {
            UnitClearCallPacket packet = new UnitClearCallPacket();
            if (Vars.net.server()) {
                packet.player = player;
            }
            
            Vars.net.send(packet, true);
        }
        
    }
    
    static void unitClear__forward(NetConnection exceptConnection, Player player) {
        if (Vars.net.server() || Vars.net.client()) {
            UnitClearCallPacket packet = new UnitClearCallPacket();
            if (Vars.net.server()) {
                packet.player = player;
            }
            
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void unitCommand(Player player) {
        if (Vars.net.server() || !Vars.net.active()) {
            InputHandler.unitCommand(player);
        }
        
        if (Vars.net.server() || Vars.net.client()) {
            UnitCommandCallPacket packet = new UnitCommandCallPacket();
            if (Vars.net.server()) {
                packet.player = player;
            }
            
            Vars.net.send(packet, true);
        }
        
    }
    
    static void unitCommand__forward(NetConnection exceptConnection, Player player) {
        if (Vars.net.server() || Vars.net.client()) {
            UnitCommandCallPacket packet = new UnitCommandCallPacket();
            if (Vars.net.server()) {
                packet.player = player;
            }
            
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void unitControl(Player player, Unit unit) {
        InputHandler.unitControl(player, unit);
        if (Vars.net.server() || Vars.net.client()) {
            UnitControlCallPacket packet = new UnitControlCallPacket();
            if (Vars.net.server()) {
                packet.player = player;
            }
            
            packet.unit = unit;
            Vars.net.send(packet, true);
        }
        
    }
    
    static void unitControl__forward(NetConnection exceptConnection, Player player, Unit unit) {
        if (Vars.net.server() || Vars.net.client()) {
            UnitControlCallPacket packet = new UnitControlCallPacket();
            if (Vars.net.server()) {
                packet.player = player;
            }
            
            packet.unit = unit;
            Vars.net.sendExcept(exceptConnection, packet, true);
        }
        
    }
    
    public static void unitDeath(int uid) {
        if (Vars.net.server() || !Vars.net.active()) {
            Units.unitDeath(uid);
        }
        
        if (Vars.net.server()) {
            UnitDeathCallPacket packet = new UnitDeathCallPacket();
            packet.uid = uid;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void unitDespawn(Unit unit) {
        if (Vars.net.server() || !Vars.net.active()) {
            Units.unitDespawn(unit);
        }
        
        if (Vars.net.server()) {
            UnitDespawnCallPacket packet = new UnitDespawnCallPacket();
            packet.unit = unit;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void unitDestroy(int uid) {
        if (Vars.net.server() || !Vars.net.active()) {
            Units.unitDestroy(uid);
        }
        
        if (Vars.net.server()) {
            UnitDestroyCallPacket packet = new UnitDestroyCallPacket();
            packet.uid = uid;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void updateGameOver(Team winner) {
        Logic.updateGameOver(winner);
        if (Vars.net.server()) {
            UpdateGameOverCallPacket packet = new UpdateGameOverCallPacket();
            packet.winner = winner;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void warningToast(int unicode, String text) {
        if (Vars.net.server()) {
            WarningToastCallPacket packet = new WarningToastCallPacket();
            packet.unicode = unicode;
            packet.text = text;
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void warningToast(NetConnection playerConnection, int unicode, String text) {
        if (Vars.net.server()) {
            WarningToastCallPacket packet = new WarningToastCallPacket();
            packet.unicode = unicode;
            packet.text = text;
            playerConnection.send(packet, true);
        }
        
    }
    
    public static void worldDataBegin() {
        if (Vars.net.server()) {
            WorldDataBeginCallPacket packet = new WorldDataBeginCallPacket();
            Vars.net.send(packet, true);
        }
        
    }
    
    public static void worldDataBegin(NetConnection playerConnection) {
        if (Vars.net.server()) {
            WorldDataBeginCallPacket packet = new WorldDataBeginCallPacket();
            playerConnection.send(packet, true);
        }
        
    }
    
    public static void registerPackets() {
        Net.registerPacket(AdminRequestCallPacket::new);
        Net.registerPacket(AnnounceCallPacket::new);
        Net.registerPacket(BeginBreakCallPacket::new);
        Net.registerPacket(BeginPlaceCallPacket::new);
        Net.registerPacket(BlockSnapshotCallPacket::new);
        Net.registerPacket(BuildingControlSelectCallPacket::new);
        Net.registerPacket(ClearItemsCallPacket::new);
        Net.registerPacket(ClientPacketReliableCallPacket::new);
        Net.registerPacket(ClientPacketUnreliableCallPacket::new);
        Net.registerPacket(ClientSnapshotCallPacket::new);
        Net.registerPacket(ConnectCallPacket::new);
        Net.registerPacket(ConnectConfirmCallPacket::new);
        Net.registerPacket(ConstructFinishCallPacket::new);
        Net.registerPacket(CreateBulletCallPacket::new);
        Net.registerPacket(CreateWeatherCallPacket::new);
        Net.registerPacket(DeconstructFinishCallPacket::new);
        Net.registerPacket(DeletePlansCallPacket::new);
        Net.registerPacket(DropItemCallPacket::new);
        Net.registerPacket(EffectCallPacket::new);
        Net.registerPacket(EffectReliableCallPacket::new);
        Net.registerPacket(EntitySnapshotCallPacket::new);
        Net.registerPacket(GameOverCallPacket::new);
        Net.registerPacket(HideHudTextCallPacket::new);
        Net.registerPacket(InfoMessageCallPacket::new);
        Net.registerPacket(InfoPopupCallPacket::new);
        Net.registerPacket(InfoToastCallPacket::new);
        Net.registerPacket(KickCallPacket::new);
        Net.registerPacket(KickCallPacket2::new);
        Net.registerPacket(LabelCallPacket::new);
        Net.registerPacket(PayloadDroppedCallPacket::new);
        Net.registerPacket(PickedBuildPayloadCallPacket::new);
        Net.registerPacket(PickedUnitPayloadCallPacket::new);
        Net.registerPacket(PingCallPacket::new);
        Net.registerPacket(PingResponseCallPacket::new);
        Net.registerPacket(PlayerDisconnectCallPacket::new);
        Net.registerPacket(PlayerSpawnCallPacket::new);
        Net.registerPacket(RemoveQueueBlockCallPacket::new);
        Net.registerPacket(RemoveTileCallPacket::new);
        Net.registerPacket(RequestBuildPayloadCallPacket::new);
        Net.registerPacket(RequestDropPayloadCallPacket::new);
        Net.registerPacket(RequestItemCallPacket::new);
        Net.registerPacket(RequestUnitPayloadCallPacket::new);
        Net.registerPacket(ResearchedCallPacket::new);
        Net.registerPacket(RotateBlockCallPacket::new);
        Net.registerPacket(SectorCaptureCallPacket::new);
        Net.registerPacket(SectorProducedCallPacket::new);
        Net.registerPacket(SendChatMessageCallPacket::new);
        Net.registerPacket(SendMessageCallPacket::new);
        Net.registerPacket(SendMessageCallPacket2::new);
        Net.registerPacket(ServerPacketReliableCallPacket::new);
        Net.registerPacket(ServerPacketUnreliableCallPacket::new);
        Net.registerPacket(SetFloorCallPacket::new);
        Net.registerPacket(SetHudTextCallPacket::new);
        Net.registerPacket(SetHudTextReliableCallPacket::new);
        Net.registerPacket(SetItemCallPacket::new);
        Net.registerPacket(SetPlayerTeamEditorCallPacket::new);
        Net.registerPacket(SetPositionCallPacket::new);
        Net.registerPacket(SetRulesCallPacket::new);
        Net.registerPacket(SetTeamCallPacket::new);
        Net.registerPacket(SetTileCallPacket::new);
        Net.registerPacket(SpawnEffectCallPacket::new);
        Net.registerPacket(StateSnapshotCallPacket::new);
        Net.registerPacket(TakeItemsCallPacket::new);
        Net.registerPacket(TileConfigCallPacket::new);
        Net.registerPacket(TileDamageCallPacket::new);
        Net.registerPacket(TileDestroyedCallPacket::new);
        Net.registerPacket(TileTapCallPacket::new);
        Net.registerPacket(TraceInfoCallPacket::new);
        Net.registerPacket(TransferInventoryCallPacket::new);
        Net.registerPacket(TransferItemEffectCallPacket::new);
        Net.registerPacket(TransferItemToCallPacket::new);
        Net.registerPacket(TransferItemToUnitCallPacket::new);
        Net.registerPacket(UnitBlockSpawnCallPacket::new);
        Net.registerPacket(UnitCapDeathCallPacket::new);
        Net.registerPacket(UnitClearCallPacket::new);
        Net.registerPacket(UnitCommandCallPacket::new);
        Net.registerPacket(UnitControlCallPacket::new);
        Net.registerPacket(UnitDeathCallPacket::new);
        Net.registerPacket(UnitDespawnCallPacket::new);
        Net.registerPacket(UnitDestroyCallPacket::new);
        Net.registerPacket(UpdateGameOverCallPacket::new);
        Net.registerPacket(WarningToastCallPacket::new);
        Net.registerPacket(WorldDataBeginCallPacket::new);
    }
}
