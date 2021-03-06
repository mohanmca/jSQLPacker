package org.jsqlpacker.parser.impl;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TStatementList;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreateFunction;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreateProcedure;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreateTrigger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jsqlpacker.parser.SQLParseException;
import org.jsqlpacker.parser.SQLParser;

public class OracleParser implements SQLParser {

	@Override
	public Collection<String> parser(String sqlFileContent)
			throws SQLParseException {
		TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
		sqlparser.setSqltext(sqlFileContent);
		int ret = sqlparser.parse();
		if (ret != 0) {
			String errorMessage = sqlparser.getErrormessage();
			throw new SQLParseException("Invalid SQL Script: " + errorMessage);
		}
		int result = sqlparser.getrawsqlstatements();
		if (result == 0) {
			List<String> sqlStatements = new ArrayList<String>();
			TStatementList list = sqlparser.sqlstatements;
			int size = list.size();
			for (int i = 0; i < size; i++) {
				TCustomSqlStatement sqlStatement = list.get(i);
				String sql = sqlStatement.toString().trim();
				boolean plSql = sqlStatement instanceof TPlsqlCreateProcedure
						|| sqlStatement instanceof TPlsqlCreateFunction
						|| sqlStatement instanceof TPlsqlCreateTrigger;
				if (sql.endsWith(";") && !(plSql)) {
					sql = sql.substring(0, sql.length() - 1);
				}
				sqlStatements.add(sql);
			}
			return sqlStatements;
		} else {
			throw new SQLParseException("Unable parse sql file");
		}
	}

}
