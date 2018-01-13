package ecg.ecg_collect.Algorithm.Filter;

public class DigitalFilter {
	
	private double[] b = new double[2];
	private double[] a = new double[2];
	private double[] x;
	private double zi;
	private double[] y;
	private double[] zf = new double[1];

	public DigitalFilter(double[] b, double[] a, double[] x, double zi)
	{
		this.b = b;
		this.a = a;
		this.x = x;
		this.zi = zi;
		y = new double[x.length];
	}
	private double[] getY()
	{
		calc();
		return y;
	}
	private double[] getZf()
	{
		calc();
		return zf;
	}
	private void calc()
	{
		for (int i = 0; i < y.length; i++)
		{
			if (i == 0)
			{
				y[i] = b[0] * x[i] + zi;
			}
			else
			{
				y[i] = b[0] * x[i] + b[1] * x[i - 1] - a[1] * y[i - 1];
				if (i == x.length - 1)
				{
					zf[0] = y[i];
				}
			}
		}
	}
	public double[] zeroFilter()
	{
		int len = x.length;    // length of input
		int nb = b.length;
		int na = a.length;
		int nfilt = Math.max(na, nb);
		int nfact = 3 * (nfilt - 1);  // length of edge transients
		//�����ֵ
		double data = 1 + a[1];
		double zi;
		zi = (b[1] - a[1] * b[0]) / data;
		//��β����
		double[] yTemp = new double[y.length + 2 * nfact];
		for (int i = 0; i < nfact; i++)
		{
			yTemp[i] = 2 * x[0] - x[nfact - i];
		}
		for (int i = nfact; i < y.length + nfact; i++)
		{
			yTemp[i] = x[i - nfact];
		}
		for (int i = y.length + nfact; i < yTemp.length; i++)
		{
			yTemp[i] = 2 * x[x.length - 1] - x[yTemp.length - i - 2 + y.length - nfact];
		}
		//
		this.zi = zi * yTemp[0];
		yTemp = zeroCalc(yTemp);
		//
		yTemp = this.reverse(yTemp);
		//
		this.zi = zi * yTemp[0];
		yTemp = zeroCalc(yTemp);
		//
		yTemp = this.reverse(yTemp);
		for (int i = 0; i < y.length; i++)
		{
			y[i] = yTemp[i + nfact];
		}
		return y;
	}
	private double[] zeroCalc(double[] xx)
	{
		double[] yy = new double[xx.length];
		for (int i = 0; i < yy.length; i++)
		{
			if (i == 0)
			{
				yy[i] = b[0] * xx[i] + zi;
			}
			else
			{
				yy[i] = b[0] * xx[i] + b[1] * xx[i - 1] - a[1] * yy[i - 1];
			}
		}
		return yy;
	}
	private double[] reverse(double[] data)
	{
		double tmp;
		for (int i = 0; i < data.length / 2; i++)
		{
			tmp = data[data.length - i - 1];
			data[data.length - i - 1] = data[i];
			data[i] = tmp;
		}
		return data;
	}
}

