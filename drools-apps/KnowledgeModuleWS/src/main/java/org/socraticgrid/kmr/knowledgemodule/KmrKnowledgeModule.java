package org.socraticgrid.kmr.knowledgemodule;

import javax.jws.WebService;

/**
 *
 * @author tmn
 */
@WebService
public interface KmrKnowledgeModule {

   public org.socraticgrid.kmr.kmtypes.ReferenceDataRefResponseType getReferenceData(org.socraticgrid.kmr.kmtypes.ReferenceDataRefRequestType param0);
   
   public org.socraticgrid.kmr.kmtypes.KmResponseType getKmByParams(org.socraticgrid.kmr.kmtypes.KmRequestType param0);

   public org.socraticgrid.kmr.kmtypes.KmMetaResponseType getKmMeta(org.socraticgrid.kmr.kmtypes.KmIdSearchRequestType param0);

   public org.socraticgrid.kmr.kmtypes.KmLatestLogicResponseType getKmLatestLogic(org.socraticgrid.kmr.kmtypes.KmIdSearchRequestType param0);

   public org.socraticgrid.kmr.kmtypes.FindKmIdsResponseType findKmIds(org.socraticgrid.kmr.kmtypes.KmRequestType param0);

   public org.socraticgrid.kmr.kmtypes.KmIdSearchResponseType getKmById(org.socraticgrid.kmr.kmtypes.KmIdSearchRequestType param0);

   public org.socraticgrid.kmr.kmtypes.KmResponseType getKmByParamsRuntime(org.socraticgrid.kmr.kmtypes.KmRequestType param0);

   public org.socraticgrid.kmr.kmtypes.KmResponseTypeImportAck insertKms(org.socraticgrid.kmr.kmtypes.KmImportRequestType param0);

   public org.socraticgrid.kmr.kmtypes.KmResponseTypeImportAck updateKms(org.socraticgrid.kmr.kmtypes.KmImportRequestType param0);

}
