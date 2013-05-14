package org.socraticgrid.kmr.knowledgemodule;

import org.socraticgrid.kmr.dao.KnowledgeModuleDAO;
import org.socraticgrid.kmr.kmtypes.ExportResponseListType;
import org.socraticgrid.kmr.kmtypes.FindKmIdsResponseListType;
import org.socraticgrid.kmr.kmtypes.FindKmIdsResponseType;
import org.socraticgrid.kmr.kmtypes.ImportResponseListType;
import org.socraticgrid.kmr.kmtypes.KmIdSearchRequestType;
import org.socraticgrid.kmr.kmtypes.KmIdSearchResponseType;
import org.socraticgrid.kmr.kmtypes.KmImportRequestType;
import org.socraticgrid.kmr.kmtypes.KmLatestLogicResponseListType;
import org.socraticgrid.kmr.kmtypes.KmLatestLogicResponseType;
import org.socraticgrid.kmr.kmtypes.KmMetaResponseListType;
import org.socraticgrid.kmr.kmtypes.KmMetaResponseType;
import org.socraticgrid.kmr.kmtypes.KmRequestType;
import org.socraticgrid.kmr.kmtypes.KmResponseType;

import org.socraticgrid.kmr.kmtypes.KmResponseTypeImportAck;
import org.socraticgrid.kmr.kmtypes.ReferenceDataRefRequestType;
import org.socraticgrid.kmr.kmtypes.ReferenceDataRefResponseType;
import org.socraticgrid.kmr.kmtypes.ReferenceDataResponseType;
import org.socraticgrid.kmr.kmtypes.RequestRefType;
import org.socraticgrid.kmr.kmtypes.ResponseListType;

import org.socraticgrid.kmr.util.DateUtils;
import javax.jws.WebService;
import javax.xml.datatype.DatatypeConfigurationException;
import org.apache.log4j.Logger;

/**
 *
 * @author tmn
 */
@WebService(endpointInterface = "org.socraticgrid.kmr.knowledgemodule.KmrKnowledgeModule",
            serviceName = "KmrKnowledgeModule")
public class KmrKnowledgeModuleImpl implements KmrKnowledgeModule {

   private final static Logger log = Logger.getLogger(KmrKnowledgeModuleImpl.class.getName());

    public KmrKnowledgeModuleImpl() {
    }

   /**
    * buildRequestRef - Create a REquestRef object for return.
    * @param reqRef
    * @return
    * @throws DatatypeConfigurationException
    */
   private RequestRefType buildRequestRef(RequestRefType reqRef) throws DatatypeConfigurationException {
      RequestRefType responseReqRef = new RequestRefType();
      DateUtils dt = new DateUtils();

      responseReqRef = reqRef;
      responseReqRef.setResponseDate(dt.getCurrDateTimeAsXMLGregorian().toString());

      return responseReqRef;
   }

   /**
    * findKmId - Given a set of params, filter on it and locate all
    *            mathcing KM_IDs.
    * @param param0
    * @return FindKmIdsResponseType
    */
   public FindKmIdsResponseType findKmIds(KmRequestType param0) {
      FindKmIdsResponseType response = new FindKmIdsResponseType();
      FindKmIdsResponseListType kms = new FindKmIdsResponseListType();

      try {
         // -----------------------------------
         // getting a list of all <KM> elements
         // -----------------------------------
         kms = KnowledgeModuleDAO.getInstance().findKmIds(param0);

         response.setResponse(kms);
         if (param0.getRequest().getRequestReference() != null) {
            response.setRequestReference(this.buildRequestRef(param0.getRequest().getRequestReference()));
         }
      } catch (Exception e) {
         log.error("FAILED findKmIds(), caught " + e.getMessage());
         e.printStackTrace();
      }

      return response;

   }

   /**
    * TODO Change to be given a list of KM_ID
    * getKmById()
    *    For each KM_ID, locate and return the matching record from
    *    table KnowledgeModule and all its metdata.
    *    The found KmFullResponse object will be added to a ResponseListType
    *    object, which will be added to the fnal return KmResponseType object.
    * 
    * @param param0
    * @return KmIdSearchResponseType
    */
   public KmIdSearchResponseType getKmById(KmIdSearchRequestType param0) {

      KmIdSearchResponseType response = new KmIdSearchResponseType();
      ExportResponseListType responseList = new ExportResponseListType();

      try {
         // -----------------------------------
         // getting a list of all <KM> elements
         // -----------------------------------
         responseList = KnowledgeModuleDAO.getInstance().findByKmId(param0);
         response.setResponseList(responseList);
         if (param0.getRequest().getRequestReference() != null) {
            response.setRequestReference(this.buildRequestRef(param0.getRequest().getRequestReference()));
         }
      } catch (Exception e) {
         log.error("FAILED getKmById(), caught " + e.getMessage());
         e.printStackTrace();
      }
      return response;
   }

   /**
    *
    * @param param0
    * @return KmResponseType
    */
   public KmResponseType getKmByParams(KmRequestType param0) {

      KmResponseType response = new KmResponseType();
      ResponseListType responseList = new ResponseListType();
      try {
         responseList = KnowledgeModuleDAO.getInstance().findByParams(param0);
         response.setResponseList(responseList);
         if (param0.getRequest().getRequestReference() != null) {
            response.setRequestReference(this.buildRequestRef(param0.getRequest().getRequestReference()));
         }
      } catch (Exception e) {
         log.error("FAILED getKmByParams(), caught " + e.getMessage());
         e.printStackTrace();
      }
      return response;
   }

   /**
    * getKmMeta() - Given KM_ID, return KM metadata
    * 
    * @param param0
    * @return KmResponseType
    */
   public KmMetaResponseType getKmMeta(KmIdSearchRequestType param0) {

      KmMetaResponseType response = new KmMetaResponseType();
      KmMetaResponseListType responseList = new KmMetaResponseListType();

      try {
         // -----------------------------------
         // getting a list of all <KM> elements into ExportResponseListType first.
         // -----------------------------------
         responseList = KnowledgeModuleDAO.getInstance().getKmMeta(param0);
         response.setResponseList(responseList);
         if (param0.getRequest().getRequestReference() != null) {
            response.setRequestReference(this.buildRequestRef(param0.getRequest().getRequestReference()));
         }
      } catch (Exception e) {
         log.error("FAILED getKmById(), caught " + e.getMessage());
         e.printStackTrace();
      }
      
      return response;
   }

   /**
    * getKmLatestLogic() - Given KM_ID, return latest KM_VERSION.Logic_NativeForm
    *
    * @param param0
    * @return KmResponseType
    */
   public KmLatestLogicResponseType getKmLatestLogic(KmIdSearchRequestType param0) {

      KmLatestLogicResponseType response = new KmLatestLogicResponseType();
      KmLatestLogicResponseListType responseList = new KmLatestLogicResponseListType();

      try {
         // -----------------------------------
         // getting a list of all <KM> elements into ExportResponseListType first.
         // -----------------------------------
         responseList = KnowledgeModuleDAO.getInstance().getKmLatestLogic(param0);
         response.setResponseList(responseList);
         if (param0.getRequest().getRequestReference() != null) {
            response.setRequestReference(this.buildRequestRef(param0.getRequest().getRequestReference()));
         }
      } catch (Exception e) {
         log.error("FAILED getKmById(), caught " + e.getMessage());
         e.printStackTrace();
      }

      return response;
   }

   /**
    *
    * @param param0
    * @return KmResponseType
    */
   public KmResponseType getKmByParamsRuntime(KmRequestType param0) {

      KmResponseType response = new KmResponseType();
      ResponseListType responseList = new ResponseListType();
      try {
         responseList = KnowledgeModuleDAO.getInstance().findByParamsForRuntime(param0);
         response.setResponseList(responseList);
         if (param0.getRequest().getRequestReference() != null) {
            response.setRequestReference(this.buildRequestRef(param0.getRequest().getRequestReference()));
         }
      } catch (Exception e) {
         log.error("FAILED getKmByParams_runtime(), caught " + e.getMessage());
         e.printStackTrace();
      }
      return response;
   }

   /**
    *
    * @param param0
    * @return KmResponseType
    */
   public KmResponseTypeImportAck insertKms(KmImportRequestType param0) {

      KmResponseTypeImportAck response = new KmResponseTypeImportAck();
      ImportResponseListType responseList = new ImportResponseListType();
      try {
         responseList = KnowledgeModuleDAO.getInstance().insertKms(param0);
         response.setAckList(responseList);
         if (param0.getRequest().getRequestReference() != null) {
            response.setRequestReference(this.buildRequestRef(param0.getRequest().getRequestReference()));
         }
      } catch (Exception e) {
         log.error("FAILED insertKms(), caught " + e.getMessage());
         e.printStackTrace();
      }
      return response;
   }

   /**
    * updateKms() - Update corresponding values for the given KMs.
    * @param param0
    * @return
    */
   public KmResponseTypeImportAck updateKms(KmImportRequestType param0) {

      KmResponseTypeImportAck response = new KmResponseTypeImportAck();
      ImportResponseListType responseList = new ImportResponseListType();
      try {
         responseList = KnowledgeModuleDAO.getInstance().updateKms(param0);
         response.setAckList(responseList);
         if (param0.getRequest().getRequestReference() != null) {
            response.setRequestReference(this.buildRequestRef(param0.getRequest().getRequestReference()));
         }
      } catch (Exception e) {
         log.error("FAILED updateKms(), caught " + e.getMessage());
         e.printStackTrace();
      }
      return response;
   }

   /**
    *
    * @param param0
    * @return KmResponseType
    */
   public ReferenceDataRefResponseType getReferenceData(ReferenceDataRefRequestType param0) {

      ReferenceDataRefResponseType response = new ReferenceDataRefResponseType();
      ReferenceDataResponseType responseList = new ReferenceDataResponseType();
      try {
         responseList = KnowledgeModuleDAO.getInstance().getRefData(param0);
         response.setResponse(responseList);
      } catch (Exception e) {
         log.error("FAILED getReferenceData(), caught " + e.getMessage());
         e.printStackTrace();
      }
      return response;
   }

   
}
