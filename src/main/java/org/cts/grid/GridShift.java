/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cts.grid;

import java.io.Serializable;

public class GridShift implements Serializable {

        private static final double METRE_PER_SECOND = 30.922080775909329D;
        private static final double RADIANS_PER_SECOND = 4.84813681109536E-06D;
        private double lon;
        private double lat;
        private double lonShift;
        private double latShift;
        private double lonAccuracy;
        private double latAccuracy;
        boolean latAccuracyAvailable;
        boolean lonAccuracyAvailable;
        private String subGridName;

        public double getLatSeconds() {
                return this.lat;
        }

        public double getLatDegrees() {
                return this.lat / 3600.0D;
        }

        public double getLatShiftSeconds() {
                return this.latShift;
        }

        public double getLatShiftDegrees() {
                return this.latShift / 3600.0D;
        }

        public double getShiftedLatSeconds() {
                return this.lat + this.latShift;
        }

        public double getShiftedLatDegrees() {
                return (this.lat + this.latShift) / 3600.0D;
        }

        public boolean isLatAccuracyAvailable() {
                return this.latAccuracyAvailable;
        }

        public double getLatAccuracySeconds() {
                if (!this.latAccuracyAvailable) {
                        throw new IllegalStateException("Latitude Accuracy not available");
                }
                return this.latAccuracy;
        }

        public double getLatAccuracyDegrees() {
                if (!this.latAccuracyAvailable) {
                        throw new IllegalStateException("Latitude Accuracy not available");
                }
                return this.latAccuracy / 3600.0D;
        }

        public double getLatAccuracyMetres() {
                if (!this.latAccuracyAvailable) {
                        throw new IllegalStateException("Latitude Accuracy not available");
                }
                return this.latAccuracy * 30.922080775909329D;
        }

        public double getLonPositiveWestSeconds() {
                return this.lon;
        }

        public double getLonPositiveEastDegrees() {
                return this.lon / -3600.0D;
        }

        public double getLonShiftPositiveWestSeconds() {
                return this.lonShift;
        }

        public double getLonShiftPositiveEastDegrees() {
                return this.lonShift / -3600.0D;
        }

        public double getShiftedLonPositiveWestSeconds() {
                return this.lon + this.lonShift;
        }

        public double getShiftedLonPositiveEastDegrees() {
                return (this.lon + this.lonShift) / -3600.0D;
        }

        public boolean isLonAccuracyAvailable() {
                return this.lonAccuracyAvailable;
        }

        public double getLonAccuracySeconds() {
                if (!this.lonAccuracyAvailable) {
                        throw new IllegalStateException("Longitude Accuracy not available");
                }
                return this.lonAccuracy;
        }

        public double getLonAccuracyDegrees() {
                if (!this.lonAccuracyAvailable) {
                        throw new IllegalStateException("Longitude Accuracy not available");
                }
                return this.lonAccuracy / 3600.0D;
        }

        public double getLonAccuracyMetres() {
                if (!this.lonAccuracyAvailable) {
                        throw new IllegalStateException("Longitude Accuracy not available");
                }
                return this.lonAccuracy * 30.922080775909329D * Math.cos(4.84813681109536E-06D * this.lat);
        }

        public void setLatSeconds(double d) {
                this.lat = d;
        }

        public void setLatDegrees(double d) {
                this.lat = (d * 3600.0D);
        }

        public void setLatAccuracyAvailable(boolean b) {
                this.latAccuracyAvailable = b;
        }

        public void setLatAccuracySeconds(double d) {
                this.latAccuracy = d;
        }

        public void setLatShiftSeconds(double d) {
                this.latShift = d;
        }

        public void setLonPositiveWestSeconds(double d) {
                this.lon = d;
        }

        public void setLonPositiveEastDegrees(double d) {
                this.lon = (d * -3600.0D);
        }

        public void setLonAccuracyAvailable(boolean b) {
                this.lonAccuracyAvailable = b;
        }

        public void setLonAccuracySeconds(double d) {
                this.lonAccuracy = d;
        }

        public void setLonShiftPositiveWestSeconds(double d) {
                this.lonShift = d;
        }

        public String getSubGridName() {
                return this.subGridName;
        }

        public void setSubGridName(String string) {
                this.subGridName = string;
        }

        public void copy(GridShift gs) {
                this.lon = gs.lon;
                this.lat = gs.lat;
                this.lonShift = gs.lonShift;
                this.latShift = gs.latShift;
                this.lonAccuracy = gs.lonAccuracy;
                this.latAccuracy = gs.latAccuracy;
                this.latAccuracyAvailable = gs.latAccuracyAvailable;
                this.lonAccuracyAvailable = gs.lonAccuracyAvailable;
                this.subGridName = gs.subGridName;
        }
}