package at.fhv.sysarch.lab3.obj;

import com.hackoeur.jglm.Vec4;

public class Face {
    private Vec4 v1;
    private Vec4 v2;
    private Vec4 v3;

    private Vec4 n1;
    private Vec4 n2;
    private Vec4 n3;

    public Face(Vec4 v1, Vec4 v2, Vec4 v3,
    Vec4 n1, Vec4 n2, Vec4 n3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;

        this.n1 = n1;
        this.n2 = n2;
        this.n3 = n3;
    }

    public Face(Vec4 v1, Vec4 v2, Vec4 v3, Face otherNormals) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;

        this.n1 = otherNormals.n1;
        this.n2 = otherNormals.n2;
        this.n3 = otherNormals.n3;
    }

    public Face(Face other) {
        // vertices are immutable value objects as well
        // therefore its ok to simply assign
        this.v1 = other.v1;
        this.v2 = other.v2;
        this.v3 = other.v3;

        this.n1 = other.n1;
        this.n2 = other.n2;
        this.n3 = other.n3;
    }

    public Vec4 getV1() {
        return this.v1;
    }

    public Vec4 getV2() {
        return this.v2;
    }

    public Vec4 getV3() {
        return this.v3;
    }

    public Vec4 getN1() {
        return this.n1;
    }

    public Vec4 getN2() {
        return this.n2;
    }

    public Vec4 getN3() {
        return this.n3;
    }
}