package org.yip.sandbox.type;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * IntArrayTypeHandler
 */
public class IntArrayTypeHandler extends BaseTypeHandler<int[]>{
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, int[] parameter, JdbcType jdbcType)	throws SQLException{
		Connection conn = ps.getConnection();
		Object[] po = new Object[parameter.length];
		int x = 0;
		for(int p:parameter){
			po[x] = Integer.valueOf(p);
			x++;
		}
		Array array = conn.createArrayOf("integer", po);
		ps.setArray(i, array);
	}

	@Override
	public int[] getNullableResult(ResultSet rs, String columnName) throws SQLException{
		return getArray(rs.getArray(columnName));
	}

	@Override
	public int[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException{
		return getArray(rs.getArray(columnIndex));
	}

	@Override
	public int[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException{
		return getArray(cs.getArray(columnIndex));
	}

	private int[] getArray(Array array){
		if (array==null){
			return null;
		}
		try{
			Object[] ary = (Object[]) array.getArray();
			int[] rtn = new int[ary.length];
			int i = 0;
			for(Object o:ary){
				rtn[i] = Integer.valueOf(o == null ? "0" : o.toString());
				i++;
			}
			return rtn;
		}catch(Exception e){
		}
		return null;
	}
}
