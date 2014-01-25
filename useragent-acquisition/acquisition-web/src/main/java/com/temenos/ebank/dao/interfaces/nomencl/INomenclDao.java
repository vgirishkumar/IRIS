package com.temenos.ebank.dao.interfaces.nomencl;

import java.util.List;

import com.temenos.ebank.domain.Nomencl;

public interface INomenclDao {
	List<Nomencl> getNomencl(String language, String group);
	public void insertNomencl( Nomencl nomencl );
	public Nomencl findNomencl( Nomencl nomencl );
}
