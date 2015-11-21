package com.Naos.Boxed;

/**
 * Created by Daimon on 6/18/2015.
 */
public class Burrito {
    private float Start, End, Position, Amount;
    private boolean bReachedEnd = false;
    private Interpolator Interp = Interpolator.sin;

/**
 *	Time-value animation class (Time in milliseconds assuming Animate()
 * 	is called every 0.016s (1s/60frames))
 */

    Burrito(float Start, float End,float Timems) {
        this.Start = Start;
        this.End = End;
        this.Position = Start;
        this.Amount = (End - Start) / (Timems/8f);
        this.Interp = Interpolator.sin;
    }
    Burrito(float Start, float End, float Timems,Interpolator interp) {
        this.Start = Start;
        this.End = End;
        this.Position = Start;
        this.Amount = (End - Start) / (Timems/8f);
        this.Interp = interp;
    }
    Burrito(float Start, float End, float SkipTo, float Timems,Interpolator interp) {
        this.Start = Start;
        this.End = End;
        this.Position = SkipTo;
        this.Amount = (End - Start) / (Timems/8f);
        this.Interp = interp;
    }

    public boolean Animate() {
        if (bReachedEnd) {
            return true;
        } else {
            Position += Amount;
        }
        return false;
    }

    public float GetValue() {
        if (bReachedEnd) {
            return End;
        } else {
            Position += Amount;
            //  Increment current position
            float Percentage = ((Position - Start) / (End - Start));
            Percentage = Math.max(Percentage,0);
            if ((End - Start) == 0) {
                // No point to conitnue
                Percentage = 0;
                bReachedEnd = true;
            }
            if (Percentage >= (End - Amount - Start) / (End - Start)) {
                // if percentage is more than (100% - percentage of one amount)
                bReachedEnd = true;
                return End;
            }
            float fReturn = Interp.Interpolate(Percentage);
            //Return current percentage
            return Start + ((End - Start) * fReturn);
        }
    }
    /*  Fixed animations (Workingperfectly now)
    *   TODO Add different graphs for animation*/
}

enum Interpolator {
	/*
	*	Float value interpolator, input must be between 0.00 and 1.00
	*/
    sin, cosin, normal,accel,decel;

    public float Interpolate(float fPercent) {
        float angle = (float)(0.5f*Math.PI) * fPercent;
        switch (this) {
            case sin:decel:
                return (float)Math.sin(angle);
            case cosin:accel:
                return 1 - (float)Math.cos(angle);
            case normal:
                return fPercent;
        }
        return (float)Math.sin(angle);
    }
}
