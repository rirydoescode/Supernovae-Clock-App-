package com.example.clock;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class HelloApplication extends Application {

    // ── Sounds ──
    private javax.sound.sampled.Clip soundBeep;
    private javax.sound.sampled.Clip soundBoom;
    private javax.sound.sampled.Clip soundSwchap;
    private javax.sound.sampled.Clip soundHum;
    private volatile boolean humPlaying = false;

    @Override
    public void start(Stage stage) {
        // ── Load sound files from resources ──
        soundBeep   = loadClip("/com/example/clock/beep.wav");
        soundBoom   = loadClip("/com/example/clock/boom.wav");
        soundSwchap = loadClip("/com/example/clock/swchap.wav");
        soundHum    = loadClip("/com/example/clock/hum.wav");
        // hum loop is set when played, not here
        Button clockBtn = new Button("Clock");
        Button stopwatchBtn = new Button("Stopwatch");
        Button timerBtn = new Button("Timer");

        clockBtn.setMaxWidth(Double.MAX_VALUE);
        stopwatchBtn.setMaxWidth(Double.MAX_VALUE);
        timerBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(clockBtn, Priority.ALWAYS);
        HBox.setHgrow(stopwatchBtn, Priority.ALWAYS);
        HBox.setHgrow(timerBtn, Priority.ALWAYS);

        String baseStyle = """
                -fx-background-radius: 20;
                -fx-background-color: #EBF2B6;
                -fx-border-color: transparent;
                -fx-font-size: 15px;
                -fx-font-family: 'Comic Sans MS';
                -fx-text-fill: #D9298A;
                -fx-padding: 8 20;
                -fx-cursor: hand;
                -fx-alignment: center;
                """;

        String activeStyle = """
                -fx-background-radius: 20;
                -fx-background-color: #C1DA92;
                -fx-border-color: transparent;
                -fx-font-size: 15px;
                -fx-font-family: 'Comic Sans MS';
                -fx-font-weight: bold;
                -fx-text-fill: #D9298A;
                -fx-padding: 8 20;
                -fx-cursor: hand;
                -fx-alignment: center;
                """;

        // --- Clock Panel ---
        Label timeLabel = new Label();
        timeLabel.setStyle("""
        -fx-font-size: 38px;
        -fx-font-family: 'Palatino Linotype';
        -fx-text-fill: #FFF5B0;
        """);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeLabel.setText(LocalTime.now().format(formatter));
        }));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
        timeLabel.setText(LocalTime.now().format(formatter));

        // --- Star Animation ---
        double canvasSize = 450 * 0.80;
        Canvas starCanvas = new Canvas(canvasSize, canvasSize);
        GraphicsContext gc = starCanvas.getGraphicsContext2D();
        final double[] angle = {0};

        Timeline starRotation = new Timeline(new KeyFrame(Duration.millis(50), e -> {
            double cs = starCanvas.getWidth();
            gc.clearRect(0, 0, cs, cs);
            double cx = cs / 2, cy = cs / 2;
            double maxR = cs / 2 * 0.85;
            long time = System.currentTimeMillis();
            java.util.Random rand = new java.util.Random(42);
            for (int i = 0; i < 25; i++) {
                double sx = rand.nextDouble() * cs;
                double sy = rand.nextDouble() * cs;
                double twinkle = 0.3 + 0.7 * Math.abs(Math.sin(time * 0.002 + i * 1.3));
                double size = 4 + rand.nextDouble() * 8.1;
                int colorPick = i % 3;
                double r = colorPick == 0 ? 255 : colorPick == 1 ? 255 : 150;
                double g = colorPick == 0 ? 255 : colorPick == 1 ? 230 : 220;
                double b = colorPick == 0 ? 255 : colorPick == 1 ? 80  : 255;
                gc.setStroke(Color.rgb((int)r, (int)g, (int)b, twinkle));
                gc.setLineWidth(1.2);
                gc.strokeLine(sx, sy - size, sx, sy + size);
                gc.strokeLine(sx - size, sy, sx + size, sy);
                gc.setLineWidth(0.6);
                gc.strokeLine(sx - size * 0.4, sy - size * 0.4, sx + size * 0.4, sy + size * 0.4);
                gc.strokeLine(sx + size * 0.4, sy - size * 0.4, sx - size * 0.4, sy + size * 0.4);
            }
            double[] glowR = {maxR, maxR * 0.82, maxR * 0.65, maxR * 0.48};
            double[] glowO = {0.12, 0.22, 0.35, 0.50};
            int[][] glowColors = {{30,20,80},{80,30,140},{160,40,180},{220,80,160}};
            for (int i = 0; i < glowR.length; i++) {
                gc.setFill(Color.rgb(glowColors[i][0], glowColors[i][1], glowColors[i][2], glowO[i]));
                gc.fillOval(cx - glowR[i], cy - glowR[i], glowR[i] * 2, glowR[i] * 2);
            }
            int numRays = 16;
            for (int i = 0; i < numRays; i++) {
                double a = Math.toRadians(angle[0] + i * (360.0 / numRays));
                double rayLen = (i % 2 == 0) ? maxR * 0.88 : maxR * 0.60;
                double rayWidth = (i % 2 == 0) ? 3.5 : 1.8;
                double x1 = cx + maxR * 0.18 * Math.cos(a);
                double y1 = cy + maxR * 0.18 * Math.sin(a);
                double x2 = cx + rayLen * Math.cos(a);
                double y2 = cy + rayLen * Math.sin(a);
                if (i % 2 == 0) gc.setStroke(Color.rgb(100, 180, 255, 0.85));
                else gc.setStroke(Color.rgb(180, 80, 255, 0.65));
                gc.setLineWidth(rayWidth);
                gc.strokeLine(x1, y1, x2, y2);
            }
            double r1 = maxR * 0.38; gc.setFill(Color.rgb(120,40,200,0.60)); gc.fillOval(cx-r1,cy-r1,r1*2,r1*2);
            double r2 = maxR * 0.28; gc.setFill(Color.rgb(200,60,160,0.75)); gc.fillOval(cx-r2,cy-r2,r2*2,r2*2);
            double r3 = maxR * 0.18; gc.setFill(Color.rgb(240,140,60,0.88)); gc.fillOval(cx-r3,cy-r3,r3*2,r3*2);
            double r4 = maxR * 0.10; gc.setFill(Color.rgb(255,220,100,0.97)); gc.fillOval(cx-r4,cy-r4,r4*2,r4*2);
            double rc = maxR * 0.055; gc.setFill(Color.rgb(255,255,255,1.0)); gc.fillOval(cx-rc,cy-rc,rc*2,rc*2);
            angle[0] += 1.5;
        }));
        starRotation.setCycleCount(Timeline.INDEFINITE);
        starRotation.play();

        StackPane clockPanel = new StackPane(starCanvas, timeLabel);
        clockPanel.setAlignment(Pos.CENTER);
        StackPane.setAlignment(timeLabel, Pos.CENTER);
        timeLabel.setTranslateY(-60);
        clockPanel.setStyle("-fx-background-color: #0D1B4B;");

        // --- Stopwatch Panel ---
        Label swTimeLabel = new Label("00 : 00 . 0");
        swTimeLabel.setStyle("""
        -fx-font-size: 38px;
        -fx-font-family: 'Palatino Linotype';
        -fx-font-style: italic;
        -fx-text-fill: #FFF5B0;
        """);

        Canvas swCanvas = new Canvas(canvasSize, canvasSize);
        GraphicsContext swGc = swCanvas.getGraphicsContext2D();
        Canvas ghostCanvas = new Canvas(canvasSize, canvasSize);
        GraphicsContext ghostGc = ghostCanvas.getGraphicsContext2D();

        java.util.List<double[]> swSparkles = new java.util.ArrayList<>();
        java.util.Random swRand = new java.util.Random();
        final long[] swElapsed = {0};
        final long[] swLastTick = {System.currentTimeMillis()};
        final boolean[] swRunning = {false};
        final int[] swLastSecond = {-1};

        double[][] sparklePalette = {
                {255,245,176},{193,218,146},{235,242,182},{255,255,255},{217,41,138}
        };

        final double[] ghostX = {canvasSize / 2};
        final double[] ghostY = {canvasSize / 2};
        final double[] ghostVx = {0.8};
        final double[] ghostVy = {0.5};
        final double[] ghostPhase = {0};
        final double ghostSize = 36;
        final double ghostMargin = ghostSize * 0.8;

        Timeline ghostTimeline = new Timeline(new KeyFrame(Duration.millis(30), e -> {
            double cs = ghostCanvas.getWidth();
            ghostGc.clearRect(0, 0, cs, cs);
            ghostPhase[0] += 0.04;
            double baseSpeed = swRunning[0] ? 2.8 : 0.6;
            double targetX = cs / 2 + Math.sin(ghostPhase[0] * 0.7) * (cs * 0.38);
            double targetY = cs / 2 + Math.cos(ghostPhase[0] * 0.5) * (cs * 0.35);
            double dx = targetX - ghostX[0];
            double dy = targetY - ghostY[0];
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist > 1) { ghostVx[0] += (dx / dist) * 0.35; ghostVy[0] += (dy / dist) * 0.35; }
            double speed = Math.sqrt(ghostVx[0] * ghostVx[0] + ghostVy[0] * ghostVy[0]);
            if (speed > baseSpeed) { ghostVx[0] = ghostVx[0] / speed * baseSpeed; ghostVy[0] = ghostVy[0] / speed * baseSpeed; }
            ghostX[0] += ghostVx[0]; ghostY[0] += ghostVy[0];
            if (ghostX[0] < ghostMargin) { ghostX[0] = ghostMargin; ghostVx[0] = Math.abs(ghostVx[0]); }
            if (ghostX[0] > cs - ghostMargin) { ghostX[0] = cs - ghostMargin; ghostVx[0] = -Math.abs(ghostVx[0]); }
            if (ghostY[0] < ghostMargin) { ghostY[0] = ghostMargin; ghostVy[0] = Math.abs(ghostVy[0]); }
            if (ghostY[0] > cs - ghostMargin) { ghostY[0] = cs - ghostMargin; ghostVy[0] = -Math.abs(ghostVy[0]); }
            double glowPulse = 0.55 + 0.45 * Math.abs(Math.sin(ghostPhase[0] * (swRunning[0] ? 3.5 : 1.2)));
            drawGlowingGhost(ghostGc, ghostX[0], ghostY[0], ghostSize, glowPulse, swRunning[0]);
        }));
        ghostTimeline.setCycleCount(Timeline.INDEFINITE);
        ghostTimeline.play();

        Timeline swTimeline = new Timeline(new KeyFrame(Duration.millis(50), e -> {
            double cs = swCanvas.getWidth();
            long now = System.currentTimeMillis();
            if (swRunning[0]) swElapsed[0] += now - swLastTick[0];
            swLastTick[0] = now;
            long totalMillis = swElapsed[0];
            long minutes = totalMillis / 60000;
            long seconds = (totalMillis / 1000) % 60;
            long tenths = (totalMillis / 100) % 10;
            swTimeLabel.setText(String.format("%02d : %02d . %d", minutes, seconds, tenths));
            int currentSecond = (int)(totalMillis / 1000);
            if (swRunning[0] && currentSecond != swLastSecond[0] && currentSecond > 0) {
                swLastSecond[0] = currentSecond;
                double sx = swRand.nextDouble() * cs;
                double sy = swRand.nextDouble() * cs;
                double size = 5 + swRand.nextDouble() * 7;
                double[] color = sparklePalette[swRand.nextInt(sparklePalette.length)];
                swSparkles.add(new double[]{sx, sy, size, now, color[0], color[1], color[2]});
            }
            swGc.clearRect(0, 0, cs, cs);
            for (double[] sp : swSparkles) {
                double age = (now - sp[3]) / 1000.0;
                double twinkle = 0.4 + 0.6 * Math.abs(Math.sin(age * 2.5));
                drawSparkle(swGc, sp[0], sp[1], sp[2], sp[4], sp[5], sp[6], twinkle);
            }
        }));
        swTimeline.setCycleCount(Timeline.INDEFINITE);
        swTimeline.play();

        Button startPauseBtn = new Button("Start");
        Button resetBtn = new Button("Reset");
        startPauseBtn.setStyle(baseStyle);
        resetBtn.setStyle(baseStyle);

        startPauseBtn.setOnAction(e -> {
            swRunning[0] = !swRunning[0];
            swLastTick[0] = System.currentTimeMillis();
            startPauseBtn.setText(swRunning[0] ? "Pause" : "Start");
        });
        resetBtn.setOnAction(e -> {
            swRunning[0] = false; swElapsed[0] = 0; swLastSecond[0] = -1;
            swSparkles.clear(); startPauseBtn.setText("Start");
            swTimeLabel.setText("00 : 00 . 0");
        });

        HBox swControls = new HBox(15, startPauseBtn, resetBtn);
        swControls.setAlignment(Pos.CENTER);

        StackPane stopwatchPanel = new StackPane(swCanvas, swTimeLabel, ghostCanvas, swControls);
        stopwatchPanel.setAlignment(Pos.CENTER);
        StackPane.setAlignment(swTimeLabel, Pos.CENTER);
        StackPane.setAlignment(ghostCanvas, Pos.TOP_LEFT);
        StackPane.setAlignment(swControls, Pos.BOTTOM_CENTER);
        StackPane.setMargin(swControls, new javafx.geometry.Insets(0, 0, 20, 0));
        swTimeLabel.setTranslateY(-100);
        stopwatchPanel.setStyle("-fx-background-color: rgba(13, 27, 75, 0.85);");

        // -------------------------------------------------------
        // --- Timer Panel ---
        // -------------------------------------------------------

        final long[] timerTotalMillis = {0};
        final long[] timerRemaining  = {0};
        final long[] timerLastTick   = {0};
        final boolean[] timerRunning  = {false};
        final boolean[] timerExploded = {false};

        // Shared black hole rotation angle
        final double[] bhAngle = {0};
        // Shockwave state: [radius, alpha]
        final double[] shockwave = {0, 0};
        // Transition: 0=none, 1=blast, 2=pullback, 3=collapse, 4=blackhole
        final int[] transPhase = {0};
        final long[] transPhaseStart = {0};
        // Supernova ejecta particles: [x, y, vx, vy, size, r, g, b, alpha]
        java.util.List<double[]> ejecta = new java.util.ArrayList<>();

        TextField hoursField   = new TextField("0");
        TextField minutesField = new TextField("0");
        TextField secondsField = new TextField("0");

        String fieldStyle = """
                -fx-background-radius: 12;
                -fx-background-color: #1A2A6C;
                -fx-text-fill: #FFF5B0;
                -fx-font-family: 'Palatino Linotype';
                -fx-font-size: 18px;
                -fx-alignment: center;
                -fx-pref-width: 55px;
                -fx-border-color: transparent;
                """;
        hoursField.setStyle(fieldStyle);
        minutesField.setStyle(fieldStyle);
        secondsField.setStyle(fieldStyle);

        Label sepLabel1 = new Label("h");
        Label sepLabel2 = new Label("m");
        Label sepLabel3 = new Label("s");
        String sepStyle = "-fx-text-fill: #FFF5B0; -fx-font-family: 'Palatino Linotype'; -fx-font-size: 18px;";
        sepLabel1.setStyle(sepStyle);
        sepLabel2.setStyle(sepStyle);
        sepLabel3.setStyle(sepStyle);

        HBox inputRow = new HBox(6, hoursField, sepLabel1, minutesField, sepLabel2, secondsField, sepLabel3);
        inputRow.setAlignment(Pos.CENTER);

        Label timerDisplay = new Label("00:00:00");
        timerDisplay.setStyle("""
                -fx-font-size: 42px;
                -fx-font-family: 'Palatino Linotype';
                -fx-text-fill: #FFF5B0;
                """);

        Canvas timerCanvas = new Canvas(canvasSize, canvasSize);
        GraphicsContext tGc = timerCanvas.getGraphicsContext2D();
        final double[] tAngle = {0};
        final double[] shakeX = {0};
        final double[] shakeY = {0};

        java.util.Random tRand = new java.util.Random();

        // Accretion disk pixel blocks: [orbitRadius, angleOffset, angularSpeed, size, r, g, b, alpha, layer]
        // We'll generate them once and store them; rotate by bhAngle each frame
        java.util.List<double[]> diskParticles = new java.util.ArrayList<>();
        java.util.Random diskRand = new java.util.Random(777);

        // Inner disk: hot white/blue-shifted (approaching side) and red-shifted (receding side)
        // Outer disk: orange, yellow, red glowing pixels
        int numDiskParticles = 180;
        for (int i = 0; i < numDiskParticles; i++) {
            // layer 0 = outer disk, layer 1 = inner disk
            int layer = i < 100 ? 0 : 1;
            double minR = layer == 0 ? 0.62 : 0.48;
            double maxRr = layer == 0 ? 0.92 : 0.62;
            double orbitR = (minR + diskRand.nextDouble() * (maxRr - minR)) * (450 * 0.80 / 2 * 0.85);
            double angleOff = diskRand.nextDouble() * Math.PI * 2;
            // angular speed: inner particles orbit faster (Keplerian)
            double angSpeed = layer == 0 ? (0.012 + diskRand.nextDouble() * 0.008)
                    : (0.022 + diskRand.nextDouble() * 0.012);
            double sz = layer == 0 ? (5 + diskRand.nextDouble() * 7)
                    : (3 + diskRand.nextDouble() * 4);

            // Colour: outer = orange/yellow/red, inner = white/cyan/blue (blue-shifted side) or orange/red
            int cr, cg, cb;
            if (layer == 0) {
                int pick = diskRand.nextInt(4);
                if (pick == 0)      { cr=255; cg=120+diskRand.nextInt(80); cb=20; }
                else if (pick == 1) { cr=255; cg=210+diskRand.nextInt(45); cb=40; }
                else if (pick == 2) { cr=220+diskRand.nextInt(35); cg=60; cb=30; }
                else                { cr=255; cg=180; cb=80; }
            } else {
                int pick = diskRand.nextInt(3);
                if (pick == 0)      { cr=255; cg=255; cb=255; }
                else if (pick == 1) { cr=100; cg=200; cb=255; }
                else                { cr=200; cg=255; cb=255; }
            }
            double alpha = 0.85 + diskRand.nextDouble() * 0.15;
            diskParticles.add(new double[]{orbitR, angleOff, angSpeed, sz, cr, cg, cb, alpha, layer});
        }

        Timeline timerCanvas_tl = new Timeline(new KeyFrame(Duration.millis(50), e -> {
            double cs = timerCanvas.getWidth();
            tGc.clearRect(0, 0, cs, cs);

            long remaining = timerRemaining[0];
            boolean exploded = timerExploded[0];
            double cx = cs / 2;
            double cy = cs / 2;
            double maxR = cs / 2 * 0.85;

            if (exploded) {
                double bhCx = cx;
                double bhCy = cy - 75;
                double ehR  = maxR * 0.42;
                long nowMs   = System.currentTimeMillis();
                long phaseAge = nowMs - transPhaseStart[0]; // ms since current phase started

                // ── Phase transitions ──
                // Phase 1 (blast): 0–2000ms  → particles fly outward
                // Phase 2 (pause): 2000–3200ms → particles slow, gravity starts
                // Phase 3 (collapse): 3200–5500ms → particles spiral inward fast
                // Phase 4 (black hole): 5500ms+ → black hole + accretion disk
                if (transPhase[0] == 1 && phaseAge > 2000) { transPhase[0] = 2; transPhaseStart[0] = nowMs; phaseAge = 0; }
                if (transPhase[0] == 2 && phaseAge > 1200) {
                    transPhase[0] = 3; transPhaseStart[0] = nowMs; phaseAge = 0;
                    // ── SWCHAP as particles start collapsing ──
                    playClip(soundSwchap);
                }
                if (transPhase[0] == 3 && phaseAge > 2300) {
                    transPhase[0] = 4; transPhaseStart[0] = nowMs; ejecta.clear();
                    // ── HUM starts as black hole appears ──
                    if (soundHum != null && !humPlaying) { soundHum.setFramePosition(0); soundHum.loop(javax.sound.sampled.Clip.LOOP_CONTINUOUSLY); humPlaying = true; }
                }

                int phase = transPhase[0];

                // ── Phases 1-3: ejecta animation ──
                if (phase == 1 || phase == 2 || phase == 3) {
                    // Shockwave still expanding in phase 1
                    if (shockwave[1] > 0.01) {
                        shockwave[0] += 5.5;
                        shockwave[1] *= 0.90;
                        tGc.setStroke(Color.rgb(255, 220, 120, shockwave[1]));
                        tGc.setLineWidth(4.0);
                        tGc.strokeOval(bhCx - shockwave[0], bhCy - shockwave[0], shockwave[0]*2, shockwave[0]*2);
                        tGc.setStroke(Color.rgb(255, 100, 200, shockwave[1] * 0.5));
                        tGc.setLineWidth(2.0);
                        tGc.strokeOval(bhCx - shockwave[0]*1.1, bhCy - shockwave[0]*1.1, shockwave[0]*2.2, shockwave[0]*2.2);
                    }

                    java.util.Iterator<double[]> it = ejecta.iterator();
                    while (it.hasNext()) {
                        double[] p = it.next();
                        double dx = bhCx - p[0];
                        double dy = bhCy - p[1];
                        double dist = Math.sqrt(dx*dx + dy*dy) + 0.001;

                        if (phase == 1) {
                            // Blast outward — just move freely, slight drag
                            p[2] *= 0.985;
                            p[3] *= 0.985;
                        } else if (phase == 2) {
                            // Decelerate and start feeling gravity
                            double t2 = phaseAge / 1200.0; // 0→1
                            double gravStrength = 0.04 * t2;
                            p[2] += (dx / dist) * gravStrength;
                            p[3] += (dy / dist) * gravStrength;
                            p[2] *= 0.97;
                            p[3] *= 0.97;
                        } else { // phase 3 — spiral collapse
                            // Strong gravity pulling inward + tangential swirl
                            double gravStrength = 0.25 + (phaseAge / 2300.0) * 0.6;
                            p[2] += (dx / dist) * gravStrength;
                            p[3] += (dy / dist) * gravStrength;
                            // Add tangential component for spiral
                            p[2] += (-dy / dist) * 0.08;
                            p[3] += ( dx / dist) * 0.08;
                            // Cap speed so they don't teleport
                            double spd = Math.sqrt(p[2]*p[2] + p[3]*p[3]);
                            if (spd > 14) { p[2] = p[2]/spd*14; p[3] = p[3]/spd*14; }
                            // Remove particles that reach the center
                            if (dist < ehR * 0.35) { it.remove(); continue; }
                            // Fade as they approach center
                            p[8] = Math.min(1.0, (dist - ehR*0.3) / (ehR * 1.5));
                        }

                        p[0] += p[2];
                        p[1] += p[3];

                        // Colour shifts: phase 3 heats particles to white/blue as they fall in
                        int pr, pg, pb;
                        if (phase == 3) {
                            double heatT = Math.min(1.0, phaseAge / 2300.0);
                            pr = (int)Math.min(255, p[5] + heatT * (255 - p[5]));
                            pg = (int)Math.min(255, p[6] + heatT * (255 - p[6]));
                            pb = (int)Math.min(255, p[7] + heatT * (255 - p[7]));
                        } else {
                            pr = (int)p[5]; pg = (int)p[6]; pb = (int)p[7];
                        }

                        double alpha = Math.max(0, p[8]);
                        if (alpha < 0.02) continue;
                        tGc.setFill(Color.rgb(pr, pg, pb, alpha));
                        tGc.fillRect(p[0] - p[4]/2, p[1] - p[4]/2, p[4], p[4]);

                        // Bright cross sparkle on big particles
                        if (p[4] > 5 && phase < 3) {
                            tGc.setStroke(Color.rgb(pr, pg, pb, alpha * 0.5));
                            tGc.setLineWidth(0.8);
                            tGc.strokeLine(p[0], p[1]-p[4], p[0], p[1]+p[4]);
                            tGc.strokeLine(p[0]-p[4], p[1], p[0]+p[4], p[1]);
                        }
                    }

                    // In phase 3 draw a growing dark core as the black hole forms
                    if (phase == 3) {
                        double coreT = Math.min(1.0, phaseAge / 2300.0);
                        double coreR = ehR * coreT * 0.7;
                        // Glow ring around forming core
                        tGc.setFill(Color.rgb(255, 140, 40, 0.25 * coreT));
                        tGc.fillOval(bhCx - coreR*1.8, bhCy - coreR*1.8, coreR*3.6, coreR*3.6);
                        tGc.setFill(Color.rgb(0, 0, 0, coreT * 0.95));
                        tGc.fillOval(bhCx - coreR, bhCy - coreR, coreR*2, coreR*2);
                    }

                    return;
                }

                // ── Phase 4: full black hole ──

                // Advance disk particle angles (Keplerian rotation)
                bhAngle[0] += 0.022;

                // Nebula remnant glow
                for (int gi = 0; gi < 3; gi++) {
                    double gradR = maxR * (0.55 + gi * 0.18);
                    int[] gc2 = gi == 0 ? new int[]{60,10,80} : gi == 1 ? new int[]{30,5,60} : new int[]{15,0,40};
                    double alpha = gi == 0 ? 0.18 : gi == 1 ? 0.10 : 0.06;
                    tGc.setFill(Color.rgb(gc2[0], gc2[1], gc2[2], alpha));
                    tGc.fillOval(bhCx - gradR, bhCy - gradR, gradR * 2, gradR * 2);
                }

                // Draw outer disk particles (behind black hole)
                for (double[] p : diskParticles) {
                    if (p[8] != 0) continue;
                    double particleAngle = p[1] + bhAngle[0] * p[2] / 0.022;
                    double px = bhCx + p[0] * Math.cos(particleAngle);
                    double py = bhCy + p[0] * Math.sin(particleAngle) * 0.32;
                    if (Math.sin(particleAngle) < 0) {
                        double dopplerShift = Math.cos(particleAngle);
                        int pr = (int)Math.min(255, p[4] + dopplerShift * 30);
                        int pg = (int)Math.min(255, p[5]);
                        int pb = (int)Math.min(255, p[6] + (-dopplerShift) * 60);
                        double distFrac = p[0] / (maxR * 0.72);
                        double brightness = 0.75 + 0.25 * distFrac;
                        double alpha = Math.min(1.0, p[7] * brightness);
                        tGc.setFill(Color.rgb(Math.max(0,pr), Math.max(0,pg), Math.max(0,pb), alpha));
                        tGc.fillRect(px - p[3]/2, py - p[3]/2, p[3], p[3]);
                    }
                }

                // Gravitational lensing rings
                double[] lensR  = {ehR * 2.8, ehR * 2.1, ehR * 1.55};
                double[] lensA  = {0.06, 0.12, 0.20};
                int[][]  lensC  = {{255,160,60},{255,120,40},{255,80,20}};
                for (int li = 0; li < lensR.length; li++) {
                    tGc.setStroke(Color.rgb(lensC[li][0], lensC[li][1], lensC[li][2], lensA[li]));
                    tGc.setLineWidth(li == 2 ? 2.5 : 1.2);
                    tGc.strokeOval(bhCx - lensR[li], bhCy - lensR[li]*0.32, lensR[li]*2, lensR[li]*0.64);
                }

                // Photon sphere
                double photonR = ehR * 1.45;
                tGc.setStroke(Color.rgb(255, 220, 120, 0.55));
                tGc.setLineWidth(1.8);
                tGc.strokeOval(bhCx - photonR, bhCy - photonR, photonR*2, photonR*2);

                // Event horizon glow + black circle
                double[] bhGlowR = {ehR*2.2, ehR*1.8, ehR*1.4, ehR*1.1};
                double[] bhGlowA = {0.08, 0.15, 0.25, 0.40};
                for (int gi = 0; gi < bhGlowR.length; gi++) {
                    tGc.setFill(Color.rgb(0, 0, 0, bhGlowA[gi]));
                    tGc.fillOval(bhCx - bhGlowR[gi], bhCy - bhGlowR[gi], bhGlowR[gi]*2, bhGlowR[gi]*2);
                }
                tGc.setFill(Color.rgb(0, 0, 0, 1.0));
                tGc.fillOval(bhCx - ehR, bhCy - ehR, ehR*2, ehR*2);

                // Front disk particles
                for (double[] p : diskParticles) {
                    double particleAngle = p[1] + bhAngle[0] * p[2] / 0.022;
                    double px = bhCx + p[0] * Math.cos(particleAngle);
                    double py = bhCy + p[0] * Math.sin(particleAngle) * 0.32;
                    boolean isFront = Math.sin(particleAngle) >= 0;
                    boolean isInner = p[8] == 1;
                    if (isFront || isInner) {
                        if (!isInner && !isFront) continue;
                        double dopplerShift = Math.cos(particleAngle);
                        int pr = (int)Math.min(255, p[4] + dopplerShift * 30);
                        int pg = (int)Math.min(255, p[5]);
                        int pb = (int)Math.min(255, p[6] + (-dopplerShift) * 60);
                        double distFrac = p[0] / (maxR * 0.72);
                        double brightness = isInner ? 1.0 : (0.6 + 0.4 * distFrac);
                        double alpha = Math.min(1.0, p[7] * brightness);
                        double distFromCenter = Math.sqrt((px-bhCx)*(px-bhCx) + (py-bhCy)*(py-bhCy));
                        if (distFromCenter < ehR * 0.9) continue;
                        tGc.setFill(Color.rgb(Math.max(0,pr), Math.max(0,pg), Math.max(0,pb), alpha));
                        tGc.fillRect(px - p[3]/2, py - p[3]/2, p[3], p[3]);
                    }
                }

                // Relativistic jets
                double jetAlpha = 0.18 + 0.10 * Math.sin(bhAngle[0] * 3.0);
                for (int ji = -1; ji <= 1; ji += 2) {
                    for (int jw = 0; jw < 3; jw++) {
                        double jetW = jw * 1.5;
                        tGc.setStroke(Color.rgb(120, 180, 255, jetAlpha * (1.0 - jw * 0.3)));
                        tGc.setLineWidth(2.5 - jw);
                        tGc.strokeLine(bhCx + jetW, bhCy + ji * ehR * 1.1,
                                bhCx + jetW * 0.3, bhCy + ji * maxR * 0.85);
                    }
                }

                return;
            }

            // --- Normal star drawing with shake ---
            double seconds = remaining / 1000.0;
            double shakeMag = 0;
            if (seconds <= 20 && timerRunning[0]) {
                shakeMag = (1 - seconds / 20.0) * 12.0;
            }
            shakeX[0] = (tRand.nextDouble() - 0.5) * shakeMag * 2;
            shakeY[0] = (tRand.nextDouble() - 0.5) * shakeMag * 2;

            double scx = cx + shakeX[0];
            double scy = cy + shakeY[0];

            long now = System.currentTimeMillis();
            java.util.Random sparkRand = new java.util.Random(42);
            for (int i = 0; i < 25; i++) {
                double sx = sparkRand.nextDouble() * cs;
                double sy = sparkRand.nextDouble() * cs;
                double twinkle = 0.3 + 0.7 * Math.abs(Math.sin(now * 0.002 + i * 1.3));
                double sz = 4 + sparkRand.nextDouble() * 8.1;
                int colorPick = i % 3;
                double rr = colorPick == 0 ? 255 : colorPick == 1 ? 255 : 150;
                double gg = colorPick == 0 ? 255 : colorPick == 1 ? 230 : 220;
                double bb = colorPick == 0 ? 255 : colorPick == 1 ? 80  : 255;
                tGc.setStroke(Color.rgb((int)rr, (int)gg, (int)bb, twinkle));
                tGc.setLineWidth(1.2);
                tGc.strokeLine(sx, sy - sz, sx, sy + sz);
                tGc.strokeLine(sx - sz, sy, sx + sz, sy);
                tGc.setLineWidth(0.6);
                tGc.strokeLine(sx - sz * 0.4, sy - sz * 0.4, sx + sz * 0.4, sy + sz * 0.4);
                tGc.strokeLine(sx + sz * 0.4, sy - sz * 0.4, sx - sz * 0.4, sy + sz * 0.4);
            }

            double[] glowR = {maxR, maxR*0.82, maxR*0.65, maxR*0.48};
            double[] glowO = {0.12, 0.22, 0.35, 0.50};
            int[][] glowColors = {{30,20,80},{80,30,140},{160,40,180},{220,80,160}};
            for (int i = 0; i < glowR.length; i++) {
                tGc.setFill(Color.rgb(glowColors[i][0], glowColors[i][1], glowColors[i][2], glowO[i]));
                tGc.fillOval(scx - glowR[i], scy - glowR[i], glowR[i] * 2, glowR[i] * 2);
            }

            int numRays = 16;
            for (int i = 0; i < numRays; i++) {
                double a = Math.toRadians(tAngle[0] + i * (360.0 / numRays));
                double rayLen = (i % 2 == 0) ? maxR * 0.88 : maxR * 0.60;
                double rayWidth = (i % 2 == 0) ? 3.5 : 1.8;
                double x1 = scx + maxR * 0.18 * Math.cos(a);
                double y1 = scy + maxR * 0.18 * Math.sin(a);
                double x2 = scx + rayLen * Math.cos(a);
                double y2 = scy + rayLen * Math.sin(a);
                if (i % 2 == 0) tGc.setStroke(Color.rgb(100, 180, 255, 0.85));
                else tGc.setStroke(Color.rgb(180, 80, 255, 0.65));
                tGc.setLineWidth(rayWidth);
                tGc.strokeLine(x1, y1, x2, y2);
            }

            double rr1 = maxR*0.38; tGc.setFill(Color.rgb(120,40,200,0.60));  tGc.fillOval(scx-rr1,scy-rr1,rr1*2,rr1*2);
            double rr2 = maxR*0.28; tGc.setFill(Color.rgb(200,60,160,0.75));  tGc.fillOval(scx-rr2,scy-rr2,rr2*2,rr2*2);
            double rr3 = maxR*0.18; tGc.setFill(Color.rgb(240,140,60,0.88));  tGc.fillOval(scx-rr3,scy-rr3,rr3*2,rr3*2);
            double rr4 = maxR*0.10; tGc.setFill(Color.rgb(255,220,100,0.97)); tGc.fillOval(scx-rr4,scy-rr4,rr4*2,rr4*2);
            double rrc = maxR*0.055; tGc.setFill(Color.rgb(255,255,255,1.0)); tGc.fillOval(scx-rrc,scy-rrc,rrc*2,rrc*2);

            tAngle[0] += 1.5;
        }));
        timerCanvas_tl.setCycleCount(Timeline.INDEFINITE);
        timerCanvas_tl.play();

        // Timer countdown logic
        Timeline timerCountdown = new Timeline(new KeyFrame(Duration.millis(50), e -> {
            if (!timerRunning[0] || timerExploded[0]) return;
            long now = System.currentTimeMillis();
            long delta = now - timerLastTick[0];
            timerLastTick[0] = now;
            timerRemaining[0] = Math.max(0, timerRemaining[0] - delta);

            long rem = timerRemaining[0];
            long h = rem / 3600000;
            long m = (rem / 60000) % 60;
            long s = (rem / 1000) % 60;
            timerDisplay.setText(String.format("%02d:%02d:%02d", h, m, s));

            // ── Beep on each of the last 5 seconds ──
            long remSeconds = rem / 1000;
            if (remSeconds >= 1 && remSeconds <= 5) {
                long prevRem = rem + delta; // what remaining was before this tick
                long prevSeconds = prevRem / 1000;
                if (prevSeconds != remSeconds) { playClip(soundBeep); }
            }

            if (rem == 0) {
                timerRunning[0] = false;
                timerExploded[0] = true;
                javafx.application.Platform.runLater(() -> timerDisplay.setVisible(false));
                // Shockwave
                shockwave[0] = timerCanvas.getWidth() * 0.06;
                shockwave[1] = 1.0;
                // Spawn ejecta particles — blast outward from center
                double ecx = timerCanvas.getWidth() / 2, ecy = timerCanvas.getWidth() / 2 - 75;
                ejecta.clear();
                int[][] ejColors = {{255,220,80},{255,140,40},{255,80,30},{255,180,100},{200,100,255},{100,200,255},{255,255,255}};
                for (int i = 0; i < 160; i++) {
                    double spAngle = tRand.nextDouble() * Math.PI * 2;
                    double spd = 2.5 + tRand.nextDouble() * 7.0;
                    double vx = Math.cos(spAngle) * spd;
                    double vy = Math.sin(spAngle) * spd;
                    double sz = 2.5 + tRand.nextDouble() * 6.5;
                    int[] col = ejColors[tRand.nextInt(ejColors.length)];
                    ejecta.add(new double[]{ecx, ecy, vx, vy, sz, col[0], col[1], col[2], 1.0});
                }
                transPhase[0] = 1;
                transPhaseStart[0] = System.currentTimeMillis();
                // ── BOOM on explosion ──
                playClip(soundBoom);
            }
        }));
        timerCountdown.setCycleCount(Timeline.INDEFINITE);
        timerCountdown.play();

        // Timer buttons
        Button timerStartBtn = new Button("Start");
        Button timerResetBtn = new Button("Reset");
        timerStartBtn.setStyle(baseStyle);
        timerResetBtn.setStyle(baseStyle);

        timerStartBtn.setOnAction(e -> {
            if (timerExploded[0]) return;
            if (!timerRunning[0] && timerRemaining[0] == 0) {
                try {
                    long h = Long.parseLong(hoursField.getText().trim());
                    long m = Long.parseLong(minutesField.getText().trim());
                    long s = Long.parseLong(secondsField.getText().trim());
                    timerRemaining[0] = (h * 3600 + m * 60 + s) * 1000;
                    timerTotalMillis[0] = timerRemaining[0];
                    if (timerRemaining[0] == 0) return;
                } catch (NumberFormatException ex) { return; }
            }
            timerRunning[0] = !timerRunning[0];
            timerLastTick[0] = System.currentTimeMillis();
            timerStartBtn.setText(timerRunning[0] ? "Pause" : "Resume");
        });

        timerResetBtn.setOnAction(e -> {
            timerRunning[0] = false;
            timerExploded[0] = false;
            timerRemaining[0] = 0;
            timerTotalMillis[0] = 0;
            shockwave[0] = 0; shockwave[1] = 0;
            bhAngle[0] = 0;
            transPhase[0] = 0; transPhaseStart[0] = 0;
            ejecta.clear();
            // ── Stop hum on reset ──
            if (soundHum != null) { soundHum.stop(); humPlaying = false; }
            timerDisplay.setText("00:00:00");
            timerDisplay.setVisible(true);
            timerStartBtn.setText("Start");
            hoursField.setText("0");
            minutesField.setText("0");
            secondsField.setText("0");
        });

        HBox timerControls = new HBox(15, timerStartBtn, timerResetBtn);
        timerControls.setAlignment(Pos.CENTER);

        VBox timerInputArea = new VBox(8, inputRow, timerControls);
        timerInputArea.setAlignment(Pos.CENTER);

        StackPane timerPanel = new StackPane(timerCanvas, timerDisplay, timerInputArea);
        timerPanel.setAlignment(Pos.CENTER);
        StackPane.setAlignment(timerDisplay, Pos.CENTER);
        timerDisplay.setTranslateY(-80);
        StackPane.setAlignment(timerInputArea, Pos.BOTTOM_CENTER);
        StackPane.setMargin(timerInputArea, new javafx.geometry.Insets(0, 0, 20, 0));
        timerPanel.setStyle("-fx-background-color: #0D1B4B;");

        // --- Main Layout ---
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0D1B4B;");

        HBox tabBar = new HBox(clockBtn, stopwatchBtn, timerBtn);
        tabBar.setAlignment(Pos.CENTER);
        tabBar.setStyle("-fx-background-color: #EBF2B6; -fx-background-radius: 20;");

        root.setTop(tabBar);
        root.setCenter(clockPanel);

        clockBtn.setStyle(activeStyle);
        stopwatchBtn.setStyle(baseStyle);
        timerBtn.setStyle(baseStyle);

        clockBtn.setOnAction(e -> {
            clockBtn.setStyle(activeStyle);
            stopwatchBtn.setStyle(baseStyle);
            timerBtn.setStyle(baseStyle);
            root.setCenter(clockPanel);
        });
        stopwatchBtn.setOnAction(e -> {
            stopwatchBtn.setStyle(activeStyle);
            clockBtn.setStyle(baseStyle);
            timerBtn.setStyle(baseStyle);
            root.setCenter(stopwatchPanel);
        });
        timerBtn.setOnAction(e -> {
            timerBtn.setStyle(activeStyle);
            clockBtn.setStyle(baseStyle);
            stopwatchBtn.setStyle(baseStyle);
            root.setCenter(timerPanel);
        });

        Scene scene = new Scene(root, 450, 450);
        stage.setTitle("Clock");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setMinWidth(320);
        stage.setMinHeight(320);
        stage.show();

        // ── Resize everything when window changes ──
        // We use the scene's width/height to drive a scale factor relative to 450
        javafx.beans.value.ChangeListener<Number> resizeListener = (obs, oldVal, newVal) -> {
            double w = scene.getWidth();
            double h = scene.getHeight();
            double size = Math.min(w, h); // keep it square-ish
            double scale = size / 450.0;

            // Resize all canvases
            starCanvas.setWidth(size); starCanvas.setHeight(size);
            swCanvas.setWidth(size);   swCanvas.setHeight(size);
            ghostCanvas.setWidth(size); ghostCanvas.setHeight(size);
            timerCanvas.setWidth(size); timerCanvas.setHeight(size);

            // Scale fonts
            int clockFont  = Math.max(14, (int)(38 * scale));
            int swFont     = Math.max(14, (int)(38 * scale));
            int timerFont  = Math.max(14, (int)(42 * scale));
            int inputFont  = Math.max(10, (int)(18 * scale));
            int btnFont    = Math.max(10, (int)(15 * scale));

            timeLabel.setStyle("-fx-font-size: " + clockFont + "px; -fx-font-family: 'Palatino Linotype'; -fx-text-fill: #FFF5B0;");
            swTimeLabel.setStyle("-fx-font-size: " + swFont + "px; -fx-font-family: 'Palatino Linotype'; -fx-font-style: italic; -fx-text-fill: #FFF5B0;");
            timerDisplay.setStyle("-fx-font-size: " + timerFont + "px; -fx-font-family: 'Palatino Linotype'; -fx-text-fill: #FFF5B0;");

            String scaledFieldStyle = "-fx-background-radius: 12; -fx-background-color: #1A2A6C; -fx-text-fill: #FFF5B0; -fx-font-family: 'Palatino Linotype'; -fx-font-size: " + inputFont + "px; -fx-alignment: center; -fx-pref-width: " + (int)(55*scale) + "px; -fx-border-color: transparent;";
            hoursField.setStyle(scaledFieldStyle);
            minutesField.setStyle(scaledFieldStyle);
            secondsField.setStyle(scaledFieldStyle);

            String scaledSepStyle = "-fx-text-fill: #FFF5B0; -fx-font-family: 'Palatino Linotype'; -fx-font-size: " + inputFont + "px;";
            sepLabel1.setStyle(scaledSepStyle);
            sepLabel2.setStyle(scaledSepStyle);
            sepLabel3.setStyle(scaledSepStyle);

            String scaledBtnStyle = "-fx-background-radius: 20; -fx-background-color: #EBF2B6; -fx-border-color: transparent; -fx-font-size: " + btnFont + "px; -fx-font-family: 'Comic Sans MS'; -fx-text-fill: #D9298A; -fx-padding: 8 20; -fx-cursor: hand; -fx-alignment: center;";
            for (javafx.scene.control.Button btn : new javafx.scene.control.Button[]{startPauseBtn, resetBtn, timerStartBtn, timerResetBtn})
                btn.setStyle(scaledBtnStyle);

            // Reposition overlaid labels
            timeLabel.setTranslateY(-60 * scale);
            swTimeLabel.setTranslateY(-100 * scale);
            timerDisplay.setTranslateY(-80 * scale);
        };

        scene.widthProperty().addListener(resizeListener);
        scene.heightProperty().addListener(resizeListener);
    }

    private javax.sound.sampled.Clip loadClip(String path) {
        try {
            java.io.InputStream is = getClass().getResourceAsStream(path);
            if (is == null) { System.err.println("Sound not found: " + path); return null; }
            javax.sound.sampled.AudioInputStream ais =
                    javax.sound.sampled.AudioSystem.getAudioInputStream(
                            new java.io.BufferedInputStream(is));
            javax.sound.sampled.Clip clip = javax.sound.sampled.AudioSystem.getClip();
            clip.open(ais);
            return clip;
        } catch (Exception e) {
            System.err.println("Could not load " + path + ": " + e.getMessage());
            return null;
        }
    }

    private void playClip(javax.sound.sampled.Clip clip) {
        if (clip == null) return;
        clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    private void drawGlowingGhost(GraphicsContext gc, double cx, double cy,
                                  double size, double glowPulse, boolean running) {
        int[] glowInner = running ? new int[]{255, 80, 200} : new int[]{120, 160, 255};
        int[] glowOuter = running ? new int[]{200, 40, 120} : new int[]{60, 80, 200};
        double[] glowRadii  = {size*2.8, size*2.1, size*1.6, size*1.2};
        double[] glowAlphas = {0.08, 0.14, 0.22, 0.32};
        for (int i = 0; i < glowRadii.length; i++) {
            double t = (double) i / (glowRadii.length - 1);
            int r = (int)(glowOuter[0] + t * (glowInner[0] - glowOuter[0]));
            int g = (int)(glowOuter[1] + t * (glowInner[1] - glowOuter[1]));
            int b = (int)(glowOuter[2] + t * (glowInner[2] - glowOuter[2]));
            double a = glowAlphas[i] * glowPulse;
            gc.setFill(Color.rgb(r, g, b, Math.min(a, 1.0)));
            double gr = glowRadii[i];
            gc.fillOval(cx - gr, cy - gr, gr * 2, gr * 2);
        }
        double haloR = size * 0.72;
        gc.setFill(Color.rgb(glowInner[0], glowInner[1], glowInner[2], 0.28 * glowPulse));
        gc.fillOval(cx - haloR, cy - haloR, haloR * 2, haloR * 2);
        double w = size, h = size * 1.2;
        int bodyR = running ? (int)(220 + 35 * glowPulse) : 255;
        int bodyG = running ? (int)(200 - 40 * glowPulse) : 245;
        int bodyB = running ? (int)(220 - 30 * glowPulse) : 250;
        gc.setFill(Color.rgb(bodyR, bodyG, bodyB, 0.93));
        gc.fillArc(cx - w/2, cy - h/2, w, w, 0, 180, javafx.scene.shape.ArcType.ROUND);
        gc.fillRect(cx - w/2, cy, w, h/2 - w*0.15);
        double waveR = w / 6;
        for (int i = 0; i < 3; i++) {
            double wx = cx - w/2 + waveR + i * (w - 2*waveR) / 2.0;
            gc.fillOval(wx - waveR, cy + h/2 - w*0.15 - waveR, waveR*2, waveR*2);
        }
        int eyeR = running ? 255 : 60;
        int eyeG = running ? 60  : 40;
        int eyeB = running ? 160 : 80;
        gc.setFill(Color.rgb(eyeR, eyeG, eyeB, 0.92));
        gc.fillOval(cx - w*0.18, cy - h*0.05, w*0.12, w*0.12);
        gc.fillOval(cx + w*0.06, cy - h*0.05, w*0.12, w*0.12);
        gc.setFill(Color.rgb(255, 255, 255, 0.80));
        gc.fillOval(cx - w*0.155, cy - h*0.035, w*0.04, w*0.04);
        gc.fillOval(cx + w*0.085, cy - h*0.035, w*0.04, w*0.04);
    }

    private void drawSparkle(GraphicsContext gc, double sx, double sy, double size,
                             double r, double g, double b, double alpha) {
        gc.setStroke(Color.rgb((int)r, (int)g, (int)b, alpha));
        gc.setLineWidth(1.2);
        gc.strokeLine(sx, sy - size, sx, sy + size);
        gc.strokeLine(sx - size, sy, sx + size, sy);
        gc.setLineWidth(0.6);
        gc.strokeLine(sx - size*0.4, sy - size*0.4, sx + size*0.4, sy + size*0.4);
        gc.strokeLine(sx + size*0.4, sy - size*0.4, sx - size*0.4, sy + size*0.4);
    }

    public static void main(String[] args) {
        launch(args);
    }
}