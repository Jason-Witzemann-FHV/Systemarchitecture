package com.hackoeur.jglm;

import java.nio.FloatBuffer;

import com.hackoeur.jglm.support.Compare;
import com.hackoeur.jglm.support.FastMath;

public final class Vec2 extends AbstractVec {
	public static final Vec2 VEC2_ZERO = new Vec2();
	
	final float x, y;
	
	public Vec2() {
		this.x = 0f;
		this.y = 0f;
	}
	
	public Vec2(final float x, final float y) {
		this.x = x;
		this.y = y;
	}
	
	public Vec2(final Vec2 vec) {
		this.x = vec.x;
		this.y = vec.y;
	}
	
	@Override
	public int getDimensions() {
		return 2;
	}

	@Override
	public float getLengthSquared() {
		return x * x + y * y;
	}
	
	public Vec2 getUnitVector() {
		final float sqLength = getLengthSquared();
		final float invLength = FastMath.invSqrtFast(sqLength);
		
		return new Vec2(x * invLength, y * invLength);
	}
	
	public Vec2 getNegated() {
		return new Vec2(-x, -y);
	}
	
	public Vec2 add(final Vec2 vec) {
		return new Vec2( x + vec.x, y + vec.y );
	}
	
	public Vec2 subtract(final Vec2 vec) {
		return new Vec2( x - vec.x, y - vec.y );
	}
	
	public Vec2 multiply(final Mat3 mat) {
		return new Vec2(
				mat.m00 * x + mat.m01 * y,
				mat.m10 * x + mat.m11 * y
		);
	}
	
	public Vec2 multiply(final float scalar) {
		return new Vec2( x * scalar, y * scalar );
	}
	
	public Vec2 scale(final float scalar) {
		return multiply(scalar);
	}

	/**
	 * @return A new vector where every value of the original vector has
	 * been multiplied with the corresponding value of the given vector.
	 */
	public Vec2 scale(final Vec2 vec) {
		return new Vec2(
				this.x * vec.x,
				this.y * vec.y
		);
	}
	
	public float dot(final Vec2 vec) {
		return this.x * vec.x + this.y * vec.y;
	}
	
	/**
	 * @param vec
	 * @return the angle between this and the given vector, in <em>radians</em>.
	 */
	public float angleInRadians(final Vec2 vec) {
		final float dot = dot(vec);
		final float lenSq = FastMath.sqrtFast( getLengthSquared() * vec.getLengthSquared() );
		return (float) FastMath.acos( dot / lenSq );
	}
	
	public Vec2 lerp(final Vec2 vec, final float amount) {
		final float diff = 1f - amount;
		return new Vec2(
				(diff*this.x + amount*vec.x),
				(diff*this.y + amount*vec.y)
		);
	}
	
	@Override
	public FloatBuffer getBuffer() {
		final FloatBuffer buffer = allocateFloatBuffer();
		final int startPos = buffer.position();
		
		buffer.put(x).put(y);
		
		buffer.position(startPos);
		
		return buffer;
	}
	/**
	 * Get the coordinates of this Vec2 as a float array.
	 *
	 * @return new float[]{x, y, z};
	 */
	public float[] getArray() {
		return new float[]{x, y};
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Vec2)) {
			return false;
		}
		
		final Vec2 other = (Vec2) obj;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x)) {
			return false;
		}
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y)) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public boolean equalsWithEpsilon(final Vec obj, final float epsilon) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null) {
			return false;
		}
		
		if (!(obj instanceof Vec2)) {
			return false;
		}
		
		final Vec2 other = (Vec2) obj;
		
		return Compare.equals(x, other.x, epsilon)
				&& Compare.equals(y, other.y, epsilon);
	}

	@Override
	public String toString() {
		return new StringBuilder()
			.append(getClass().getSimpleName())
			.append("{")
			.append(String.format("%8.5f %8.5f", x, y))
			.append("}")
			.toString();
	}
}
