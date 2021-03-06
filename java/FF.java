/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

/* Java Large Finite Field arithmetic */
/* AMCL mod p functions */

public final class FF {
	private final BIG[] v;
	private final int length;

	private static final int P_MBITS=ROM.MODBYTES*8;
	private static final int P_MB=(P_MBITS%ROM.BASEBITS);
	private static final int P_OMASK=((int)(-1)<<(P_MBITS%ROM.BASEBITS));
	private static final int P_FEXCESS=((int)1<<(ROM.BASEBITS*ROM.NLEN-P_MBITS));
	private static final int P_TBITS=(P_MBITS%ROM.BASEBITS);

	public int P_EXCESS()
	{
		return ((v[length-1].get(ROM.NLEN-1)&P_OMASK)>>(P_MB));
	}

/* Constructors */
	public FF(int n)
	{
		v=new BIG[n];
		for (int i=0;i<n;i++)
			v[i]=new BIG(0);
		length=n;
	}

	public FF(int [][] x,int n)
	{
		v=new BIG[n];
		for (int i=0;i<n;i++)
			v[i]=new BIG(x[i]);
		length=n;
	}

	public int getlen()
	{
		return length;
	}

/* set to integer */
	public void set(int m)
	{
		zero();
		v[0].set(0,(int)(m&ROM.MASK));
		v[0].set(1,(int)(m>>ROM.BASEBITS));
	}

/* copy from FF b */
	public void copy(FF b)
	{
		for (int i=0;i<length;i++)
		{
			v[i].copy(b.v[i]);
		}
	}

/* x=y<<n */
	public void dsucopy(FF b)
	{
		for (int i=0;i<b.length;i++)
		{
			v[b.length+i].copy(b.v[i]);
			v[i].zero();
		}
	}

/* x=y */
	public void dscopy(FF b)
	{
		for (int i=0;i<b.length;i++)
		{
			v[i].copy(b.v[i]);
			v[b.length+i].zero();
		}
	}

/* x=y>>n */
	public void sducopy(FF b)
	{
		for (int i=0;i<length;i++)
		{
			v[i].copy(b.v[length+i]);
		}
	}

/* set to zero */
	public void zero()
	{
		for (int i=0;i<length;i++)
		{
			v[i].zero();
		}
	}

	public void one()
	{
		v[0].one();
		for (int i=1;i<length;i++)
		{
			v[i].zero();
		}
	}

/* test equals 0 */
	public boolean iszilch()
	{
		for (int i=0;i<length;i++)
		{
			if (!v[i].iszilch()) return false;
		}
		return true;
	}

/* shift right by 256-bit words */
	public void shrw(int n)
	{
		for (int i=0;i<n;i++)
		{
			v[i].copy(v[i+n]);
			v[i+n].zero();
		}
	}

/* shift left by 256-bit words */
	public void shlw(int n)
	{
		for (int i=0;i<n;i++)
		{
			v[n+i].copy(v[i]);
			v[i].zero();
		}
	}

/* extract last bit */
	public int parity()
	{
		return v[0].parity();
	}

	public int lastbits(int m)
	{
		return v[0].lastbits(m);
	}

/* compare x and y - must be normalised, and of same length */
	public static int comp(FF a,FF b)
	{
		int i,j;
		for (i=a.length-1;i>=0;i--)
		{
			j=BIG.comp(a.v[i],b.v[i]);
			if (j!=0) return j;
		}
		return 0;
	}

/* recursive add */
	public void radd(int vp,FF x,int xp,FF y,int yp,int n)
	{
		for (int i=0;i<n;i++)
		{
			v[vp+i].copy(x.v[xp+i]);
			v[vp+i].add(y.v[yp+i]);
		}
	}

/* recursive inc */
	public void rinc(int vp,FF y,int yp,int n)
	{
		for (int i=0;i<n;i++)
		{
			v[vp+i].add(y.v[yp+i]);
		}
	}

/* recursive sub */
	public void rsub(int vp,FF x,int xp,FF y,int yp,int n)
	{
		for (int i=0;i<n;i++)
		{
			v[vp+i].copy(x.v[xp+i]);
			v[vp+i].sub(y.v[yp+i]);
		}
	}

/* recursive dec */
	public void rdec(int vp,FF y,int yp,int n)
	{
		for (int i=0;i<n;i++)
		{
			v[vp+i].sub(y.v[yp+i]);
		}
	}

/* simple add */
	public void add(FF b)
	{
		for (int i=0;i<length;i++)
			v[i].add(b.v[i]);
	}

/* simple sub */
	public void sub(FF b)
	{
		for (int i=0;i<length;i++)
			v[i].sub(b.v[i]);
	}

/* reverse sub */
	public void revsub(FF b)
	{
		for (int i=0;i<length;i++)
			v[i].rsub(b.v[i]);
	}

/* increment/decrement by a small integer */
	public void inc(int m)
	{
		v[0].inc(m);
		norm();
	}

	public void dec(int m)
	{
		v[0].dec(m);
		norm();
	}

	/* normalise - but hold any overflow in top part unless n<0 */
	private void rnorm(int vp,int n)
	{
		boolean trunc=false;
		int i,carry;
		if (n<0)
		{ /* -v n signals to do truncation */
			n=-n;
			trunc=true;
		}
		for (i=0;i<n-1;i++)
		{
			carry=v[vp+i].norm();
			v[vp+i].xortop(carry<<P_TBITS);
			v[vp+i+1].inc(carry);
		}
		carry=v[vp+n-1].norm();
		if (trunc)
			v[vp+n-1].xortop(carry<<P_TBITS);

	}

	public void norm()
	{
		rnorm(0,length);
	}

/* shift left by one bit */
	public void shl()
	{
		int i,carry,delay_carry=0;
		for (i=0;i<length-1;i++)
		{
			carry=v[i].fshl(1);
			v[i].inc(delay_carry);
			v[i].xortop(carry<<P_TBITS);
			delay_carry=carry;
		}
		v[length-1].fshl(1);
		v[length-1].inc(delay_carry);
	}

/* shift right by one bit */

	public void shr()
	{
		int i,carry;
		for (i=length-1;i>0;i--)
		{
			carry=v[i].fshr(1);
			v[i-1].ortop(carry<<P_TBITS);
		}
		v[0].fshr(1);
	}

/* Convert to Hex String */
	public String toString()
	{
		norm();
		String s="";
		for (int i=length-1;i>=0;i--)
		{
			s+=v[i].toString();
		}
		return s;
	}

/* Convert FFs to/from byte arrays */
	public void toBytes(byte[] b)
	{
		for (int i=0;i<length;i++)
		{
			v[i].tobytearray(b,(length-i-1)*ROM.MODBYTES);
		}
	}

	public static void fromBytes(FF x,byte[] b)
	{
		for (int i=0;i<x.length;i++)
		{
			x.v[i]=BIG.frombytearray(b,(x.length-i-1)*ROM.MODBYTES);
		}
	}

/* in-place swapping using xor - side channel resistant - lengths must be the same */
	private static void cswap(FF a,FF b,int d)
	{
		for (int i=0;i<a.length;i++)
		{
		//	BIG.cswap(a.v[i],b.v[i],d);
			a.v[i].cswap(b.v[i],d);
		}
	}

/* z=x*y, t is workspace */
	private void karmul(int vp,FF x,int xp,FF y,int yp,FF t,int tp,int n)
	{
		int nd2;
		if (n==1)
		{
			DBIG d=BIG.mul(x.v[xp],y.v[yp]);
			v[vp+1]=d.split(8*ROM.MODBYTES);
			v[vp].copy(d);
			return;
		}
		nd2=n/2;
		radd(vp,x,xp,x,xp+nd2,nd2);
		rnorm(vp,nd2);
		radd(vp+nd2,y,yp,y,yp+nd2,nd2);
		rnorm(vp+nd2,nd2);
		t.karmul(tp,this,vp,this,vp+nd2,t,tp+n,nd2);
		karmul(vp,x,xp,y,yp,t,tp+n,nd2);
		karmul(vp+n,x,xp+nd2,y,yp+nd2,t,tp+n,nd2);
		t.rdec(tp,this,vp,n);
		t.rdec(tp,this,vp+n,n);
		rinc(vp+nd2,t,tp,n);
		rnorm(vp,2*n);
	}

	private void karsqr(int vp,FF x,int xp,FF t,int tp,int n)
	{
		int nd2;
		if (n==1)
		{
			DBIG d=BIG.sqr(x.v[xp]);
			v[vp+1].copy(d.split(8*ROM.MODBYTES));
			v[vp].copy(d);
			return;
		}

		nd2=n/2;
		karsqr(vp,x,xp,t,tp+n,nd2);
		karsqr(vp+n,x,xp+nd2,t,tp+n,nd2);
		t.karmul(tp,x,xp,x,xp+nd2,t,tp+n,nd2);
		rinc(vp+nd2,t,tp,n);
		rinc(vp+nd2,t,tp,n);
		rnorm(vp+nd2,n);
	}


	private void karmul_lower(int vp,FF x,int xp,FF y,int yp,FF t,int tp,int n)
	{ /* Calculates Least Significant bottom half of x*y */
		int nd2;
		if (n==1)
		{ /* only calculate bottom half of product */
			v[vp].copy(BIG.smul(x.v[xp],y.v[yp]));
			return;
		}
		nd2=n/2;

		karmul(vp,x,xp,y,yp,t,tp+n,nd2);
		t.karmul_lower(tp,x,xp+nd2,y,yp,t,tp+n,nd2);
		rinc(vp+nd2,t,tp,nd2);
		t.karmul_lower(tp,x,xp,y,yp+nd2,t,tp+n,nd2);
		rinc(vp+nd2,t,tp,nd2);
		rnorm(vp+nd2,-nd2);  /* truncate it */
	}

	private void karmul_upper(FF x,FF y,FF t,int n)
	{ /* Calculates Most Significant upper half of x*y, given lower part */
		int nd2;

		nd2=n/2;
		radd(n,x,0,x,nd2,nd2);
		radd(n+nd2,y,0,y,nd2,nd2);

		t.karmul(0,this,n+nd2,this,n,t,n,nd2);  /* t = (a0+a1)(b0+b1) */
		karmul(n,x,nd2,y,nd2,t,n,nd2); /* z[n]= a1*b1 */
									/* z[0-nd2]=l(a0b0) z[nd2-n]= h(a0b0)+l(t)-l(a0b0)-l(a1b1) */
		t.rdec(0,this,n,n);              /* t=t-a1b1  */
		rinc(nd2,this,0,nd2);   /* z[nd2-n]+=l(a0b0) = h(a0b0)+l(t)-l(a1b1)  */
		rdec(nd2,t,0,nd2);   /* z[nd2-n]=h(a0b0)+l(t)-l(a1b1)-l(t-a1b1)=h(a0b0) */
		rnorm(0,-n);					/* a0b0 now in z - truncate it */
		t.rdec(0,this,0,n);         /* (a0+a1)(b0+b1) - a0b0 */
		rinc(nd2,t,0,n);

		rnorm(nd2,n);
	}

	/* z=x*y. Assumes x and y are of same length. */
	public static FF mul(FF x,FF y)
	{
		int n=x.length;
		FF z=new FF(2*n);
		FF t=new FF(2*n);
		z.karmul(0,x,0,y,0,t,0,n);
		return z;
	}

	/* z=x^2 */
	public static FF sqr(FF x)
	{
		int n=x.length;
		FF z=new FF(2*n);
		FF t=new FF(2*n);
		z.karsqr(0,x,0,t,0,n);
		return z;
	}

/* return low part of product this*y */
	public void lmul(FF y)
	{
		int n=length;
		FF t=new FF(2*n);
		FF x=new FF(n); x.copy(this);
		karmul_lower(0,x,0,y,0,t,0,n);
	}

/* Set b=b mod c */
	public void mod(FF c)
	{
		int k=0;

		norm();
		if (comp(this,c)<0)
			return;
		do
		{
			c.shl();
			k++;
		} while (comp(this,c)>=0);

		while (k>0)
		{
			c.shr();
			if (comp(this,c)>=0)
			{
				sub(c);
				norm();
			}
			k--;
		}
	}

/* return This mod modulus, N is modulus, ND is Montgomery Constant */
	public FF reduce(FF N,FF ND)
	{ /* fast karatsuba Montgomery reduction */
		int n=N.length;
		FF t=new FF(2*n);
		FF r=new FF(n);
		FF m=new FF(n);

		r.sducopy(this);
		m.karmul_lower(0,this,0,ND,0,t,0,n);
		karmul_upper(N,m,t,n);
		m.sducopy(this);

		r.add(N);
		r.sub(m);
		r.norm();

		return r;

	}

/* Set r=this mod b */
/* this is of length - 2*n */
/* r,b is of length - n */
	public FF dmod(FF b)
	{
		int k,n=b.length;
		FF m=new FF(2*n);
		FF x=new FF(2*n);
		FF r=new FF(n);

		x.copy(this);
		x.norm();
		m.dsucopy(b); k=256*n;

		while (k>0)
		{
			m.shr();

			if (comp(x,m)>=0)
			{
				x.sub(m);
				x.norm();
			}
			k--;
		}

		r.copy(x);
		r.mod(b);
		return r;
	}

/* Set return=1/this mod p. Binary method - a<p on entry */

	public void invmodp(FF p)
	{
		int n=p.length;

		FF u=new FF(n);
		FF v=new FF(n);
		FF x1=new FF(n);
		FF x2=new FF(n);
		FF t=new FF(n);
		FF one=new FF(n);

		one.one();
		u.copy(this);
		v.copy(p);
		x1.copy(one);
		x2.zero();

	// reduce n in here as well!
		while (comp(u,one)!=0 && comp(v,one)!=0)
		{
			while (u.parity()==0)
			{
				u.shr();
				if (x1.parity()!=0)
				{
					x1.add(p);
					x1.norm();
				}
				x1.shr();
			}
			while (v.parity()==0)
			{
				v.shr();
				if (x2.parity()!=0)
				{
					x2.add(p);
					x2.norm();
				}
				x2.shr();
			}
			if (comp(u,v)>=0)
			{

				u.sub(v);
				u.norm();
				if (comp(x1,x2)>=0) x1.sub(x2);
				else
				{
					t.copy(p);
					t.sub(x2);
					x1.add(t);
				}
				x1.norm();
			}
			else
			{
				v.sub(u);
				v.norm();
				if (comp(x2,x1)>=0) x2.sub(x1);
				else
				{
					t.copy(p);
					t.sub(x1);
					x2.add(t);
				}
				x2.norm();
			}
		}
		if (comp(u,one)==0)
			copy(x1);
		else
			copy(x2);
	}

/* nresidue mod m */
	public void nres(FF m)
	{
		int n=m.length;
		FF d=new FF(2*n);
		d.dsucopy(this);
		copy(d.dmod(m));
	}

	public void redc(FF m,FF ND)
	{
		int n=m.length;
		FF d=new FF(2*n);
		mod(m);
		d.dscopy(this);
		copy(d.reduce(m,ND));
		mod(m);
	}

	private void mod2m(int m)
	{
		for (int i=m;i<length;i++)
			v[i].zero();
	}

	/* U=1/a mod 2^m - Arazi & Qi */
	private FF invmod2m()
	{
		int i,n=length;

		FF b=new FF(n);
		FF c=new FF(n);
		FF U=new FF(n);

		FF t;

		U.zero();
		U.v[0].copy(v[0]);
		U.v[0].invmod2m();

		for (i=1;i<n;i<<=1)
		{
			b.copy(this); b.mod2m(i);
			t=mul(U,b); t.shrw(i); b.copy(t);
			c.copy(this); c.shrw(i); c.mod2m(i);
			c.lmul(U); c.mod2m(i);

			b.add(c); b.norm();
			b.lmul(U); b.mod2m(i);

			c.one(); c.shlw(i); b.revsub(c); b.norm();
			b.shlw(i);
			U.add(b);
		}
		U.norm();
		return U;
	}

	public void random(RAND rng)
	{
		int n=length;
		for (int i=0;i<n;i++)
		{
			v[i].copy(BIG.random(rng));
		}
	/* make sure top bit is 1 */
		while (v[n-1].nbits()<ROM.MODBYTES*8) v[n-1].copy(BIG.random(rng));
	}

	/* generate random x */
	public void randomnum(FF p,RAND rng)
	{
		int n=length;
		FF d=new FF(2*n);

		for (int i=0;i<2*n;i++)
		{
			d.v[i].copy(BIG.random(rng));
		}
		copy(d.dmod(p));
	}

	/* this*=y mod p */
	public void modmul(FF y,FF p,FF nd)
	{
		int ex=P_EXCESS();
		int ey=y.P_EXCESS();
		if ((ex+1)*(ey+1)+1>=P_FEXCESS) mod(p);
		FF d=mul(this,y);
		copy(d.reduce(p,nd));
	}

	/* this*=y mod p */
	public void modsqr(FF p,FF nd)
	{
		int ex=P_EXCESS();
		if ((ex+1)*(ex+1)+1>=P_FEXCESS) mod(p);
		FF d=sqr(this);
		copy(d.reduce(p,nd));
	}

	/* this=this^e mod p using side-channel resistant Montgomery Ladder, for large e */
	public void skpow(FF e,FF p)
	{
		int i,b,n=p.length;
		FF R0=new FF(n);
		FF R1=new FF(n);
		FF ND=p.invmod2m();

		mod(p);
		R0.one();
		R1.copy(this);
		R0.nres(p);
		R1.nres(p);

		for (i=8*ROM.MODBYTES*n-1;i>=0;i--)
		{
			b=e.v[i/256].bit(i%256);
			copy(R0);
			modmul(R1,p,ND);

			cswap(R0,R1,b);
			R0.modsqr(p,ND);

			R1.copy(this);
			cswap(R0,R1,b);

		}

		copy(R0);
		redc(p,ND);
	}

	/* this =this^e mod p using side-channel resistant Montgomery Ladder, for short e */
	public void skpow(BIG e,FF p)
	{
		int i,b,n=p.length;
		FF R0=new FF(n);
		FF R1=new FF(n);
		FF ND=p.invmod2m();

		mod(p);
		R0.one();
		R1.copy(this);
		R0.nres(p);
		R1.nres(p);

		for (i=8*ROM.MODBYTES-1;i>=0;i--)
		{
			b=e.bit(i);
			copy(R0);
			modmul(R1,p,ND);

			cswap(R0,R1,b);
			R0.modsqr(p,ND);

			R1.copy(this);
			cswap(R0,R1,b);
		}
		copy(R0);
		redc(p,ND);
	}

	/* raise to an integer power - right-to-left method */
	public void power(int e,FF p)
	{
		int n=p.length;
		boolean f=true;
		FF w=new FF(n);
		FF ND=p.invmod2m();

		w.copy(this);
		w.nres(p);

		if (e==2)
		{
			copy(w);
			modsqr(p,ND);
		}
		else for (; ; )
		{
			if (e%2==1)
			{
				if (f) copy(w);
				else modmul(w,p,ND);
				f=false;
			}
			e>>=1;
			if (e==0) break;
			w.modsqr(p,ND);
		}
		redc(p,ND);
	}

	/* this=this^e mod p, faster but not side channel resistant */
	public void pow(FF e,FF p)
	{
		int i,b,n=p.length;
		FF w=new FF(n);
		FF ND=p.invmod2m();

		w.copy(this);
		one();
		nres(p);
		w.nres(p);
		for (i=8*ROM.MODBYTES*n-1;i>=0;i--)
		{
			modsqr(p,ND);
			b=e.v[i/256].bit(i%256);
			if (b==1) modmul(w,p,ND);
		}
		redc(p,ND);
	}

	/* double exponentiation r=x^e.y^f mod p */
	public void pow2(BIG e,FF y,BIG f,FF p)
	{
		int i,eb,fb,n=p.length;
		FF xn=new FF(n);
		FF yn=new FF(n);
		FF xy=new FF(n);
		FF ND=p.invmod2m();

		xn.copy(this);
		yn.copy(y);
		xn.nres(p);
		yn.nres(p);
		xy.copy(xn); xy.modmul(yn,p,ND);
		one();
		nres(p);

		for (i=8*ROM.MODBYTES-1;i>=0;i--)
		{
			eb=e.bit(i);
			fb=f.bit(i);
			modsqr(p,ND);
			if (eb==1)
			{
				if (fb==1) modmul(xy,p,ND);
				else modmul(xn,p,ND);
			}
			else
			{
				if (fb==1) modmul(yn,p,ND);
			}
		}
		redc(p,ND);
	}

	private static int igcd(int x,int y)
	{ /* integer GCD, returns GCD of x and y */
		int r;
		if (y==0) return x;
		while ((r=x%y)!=0)
			{x=y;y=r;}
		return y;
	}

	/* quick and dirty check for common factor with n */
	public boolean cfactor(int s)
	{
		int r,n=length;
		int g;

		FF x=new FF(n);
		FF y=new FF(n);

		y.set(s);
		x.copy(this);
		x.norm();

		do
		{
			x.sub(y);
			x.norm();
			while (!x.iszilch() && x.parity()==0) x.shr();
		}
		while (comp(x,y)>0);

		g=x.v[0].get(0);
		r=igcd(s,g);
		if (r>1) return true;
		return false;
	}

	/* Miller-Rabin test for primality. Slow. */
	public static boolean prime(FF p,RAND rng)
	{
		int i,j,s=0,n=p.length;
		boolean loop;
		FF d=new FF(n);
		FF x=new FF(n);
		FF unity=new FF(n);
		FF nm1=new FF(n);

		int sf=4849845; /* 3*5*.. *19 */
		p.norm();

		if (p.cfactor(sf)) return false;
		unity.one();
		nm1.copy(p);
		nm1.sub(unity);
		nm1.norm();
		d.copy(nm1);

		while (d.parity()==0)
		{
			d.shr();
			s++;
		}
		if (s==0) return false;
		for (i=0;i<10;i++)
		{
			x.randomnum(p,rng);
			x.pow(d,p);
			if (comp(x,unity)==0 || comp(x,nm1)==0) continue;
			loop=false;
			for (j=1;j<s;j++)
			{
				x.power(2,p);
				if (comp(x,unity)==0) return false;
				if (comp(x,nm1)==0) {loop=true; break;}
			}
			if (loop) continue;
			return false;
		}
		return true;
	}


//	public static final int[][] P ={{0x1670957,0x1568CD3C,0x2595E5,0xEED4F38,0x1FC9A971,0x14EF7E62,0xA503883,0x9E1E05E,0xBF59E3},{0x1844C908,0x1B44A798,0x3A0B1E7,0xD1B5B4E,0x1836046F,0x87E94F9,0x1D34C537,0xF7183B0,0x46D07},{0x17813331,0x19E28A90,0x1473A4D6,0x1CACD01F,0x1EEA8838,0xAF2AE29,0x1F85292A,0x1632585E,0xD945E5},{0x919F5EF,0x1567B39F,0x19F6AD11,0x16CE47CF,0x9B36EB1,0x35B7D3,0x483B28C,0xCBEFA27,0xB5FC21}};
/*
	public static final int[][] P= {{0x156FFDDF,0x5EC1ED,0xC6702D0,0x1C42FB6,0x1A3A50F0,0x1EE1811F,0x1AB28D94,0x1BE439E1,0x56790},{0x1D781CB3,0x1E3D731B,0x153A96F6,0x9AC443F,0x10628677,0x1F21365D,0x97B4301,0xDAD3A12,0xD6C46E},{0x833D55D,0xD44CF7B,0x4373422,0x22718D3,0x1E4CF3CA,0xB774703,0x117E2980,0x10C0F2A7,0x10D8B7},{0x1A784949,0x1483C3BF,0x7938D16,0x18E0E7B5,0x111E4EF6,0x20163B4,0x95FEBE,0x129E8526,0xF48167}};

	public static void main(String[] args) {
		byte[] raw=new byte[100];
		RAND rng=new RAND();

		rng.clean();
		for (int i=0;i<100;i++) raw[i]=(byte)i;

		rng.seed(100,raw);

		int n=4;

		FF x=new FF(n);
		x.set(3);

		FF p=new FF(P,n);

	//	if (prime(p,rng)) System.out.println("p is a prime");

		FF e=new FF(n);
		e.copy(p);
		e.dec(1); e.norm();

		System.out.println("e= "+e.toString());

		x.skpow(e,p);
		System.out.println("x= "+x.toString());

    } */

}
