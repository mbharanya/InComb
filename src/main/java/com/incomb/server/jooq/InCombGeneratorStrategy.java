package com.incomb.server.jooq;

import org.jooq.util.DefaultGeneratorStrategy;
import org.jooq.util.Definition;

/**
 * The GeneratorStrategy for the jooq class generation.
 */
public class InCombGeneratorStrategy extends DefaultGeneratorStrategy {

	/**
	 * Daos start with "Internal". e.g. NewsDao -> InternalNewsDao
	 * Tables end with "Table". e.g. News -> NewsTable
	 */
	@Override
	public String getJavaClassName(final Definition definition, final Mode mode) {
		String className = super.getJavaClassName(definition, mode);

		if(mode == Mode.DAO) {
			className = "Internal" + className;
		}
		else if (mode == Mode.DEFAULT) {
			className += "Table";
		}

		return className;
	}

	/**
	 * Tables are in the <code>tables</code> package. <br />
	 * Daos are in the <code>dao.internal</code> package. <br />
	 * Records are in the <code>records</code> package. <br />
	 */
	@Override
	public String getJavaPackageName(final Definition definition, final Mode mode) {
		final StringBuilder sb = new StringBuilder(getTargetPackage());

		if (mode == Mode.DEFAULT) {
			sb.append(".tables");
		}
		else if (mode == Mode.DAO) {
			sb.append(".dao.internal");
		}
		else if (mode == Mode.RECORD) {
			sb.append(".records");
		}

		return sb.toString();
	}
}
