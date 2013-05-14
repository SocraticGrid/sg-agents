package org.socraticgrid.kmr.dao;

import org.socraticgrid.kmr.kmtypes.ExportResponseListType;
import org.socraticgrid.kmr.kmtypes.FindKmIdsResponseListType;
import org.socraticgrid.kmr.kmtypes.ImportResponseListType;
import org.socraticgrid.kmr.kmtypes.KmIdSearchRequestType;
import org.socraticgrid.kmr.kmtypes.KmImportRequestType;
import org.socraticgrid.kmr.kmtypes.KmLatestLogicResponseListType;
import org.socraticgrid.kmr.kmtypes.KmMetaResponseListType;
import org.socraticgrid.kmr.kmtypes.KmRequestType;
import org.socraticgrid.kmr.kmtypes.ReferenceDataRefRequestType;
import org.socraticgrid.kmr.kmtypes.ReferenceDataResponseType;
import org.socraticgrid.kmr.kmtypes.ResponseListType;


/**
 *
 * @author tmn
 */
public interface KnowledgeModuleManager {
   public ReferenceDataResponseType getRefData(ReferenceDataRefRequestType params);
   public KmMetaResponseListType getKmMeta(KmIdSearchRequestType params);
   public KmLatestLogicResponseListType getKmLatestLogic(KmIdSearchRequestType params);
   
   public FindKmIdsResponseListType findKmIds(KmRequestType params);
   public ExportResponseListType findByKmId(KmIdSearchRequestType params);
   public ResponseListType findByParams(KmRequestType params);
   public ResponseListType findByParamsForRuntime(KmRequestType params);
   public ImportResponseListType insertKms(KmImportRequestType params);
   public ImportResponseListType updateKms(KmImportRequestType params);
}
