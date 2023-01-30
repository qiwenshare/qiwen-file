/**
 *
 * (c) Copyright Ascensio System SIA 2021
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.qiwenshare.file.office.services.configurers.implementations;

import com.alibaba.fastjson2.JSON;
import com.qiwenshare.file.office.documentserver.managers.jwt.JwtManager;
import com.qiwenshare.file.office.documentserver.models.enums.Action;
import com.qiwenshare.file.office.documentserver.models.enums.DocumentType;
import com.qiwenshare.file.office.documentserver.models.filemodel.FileModel;
import com.qiwenshare.file.office.documentserver.models.filemodel.Permission;
import com.qiwenshare.file.office.documentserver.util.file.FileUtility;
import com.qiwenshare.file.office.mappers.Mapper;
import com.qiwenshare.file.office.services.configurers.FileConfigurer;
import com.qiwenshare.file.office.services.configurers.wrappers.DefaultDocumentWrapper;
import com.qiwenshare.file.office.services.configurers.wrappers.DefaultFileWrapper;
import org.primeframework.jwt.domain.JWT;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Primary
public class DefaultFileConfigurer implements FileConfigurer<DefaultFileWrapper> {
    @Autowired
    private ObjectFactory<FileModel> fileModelObjectFactory;

    @Autowired
    private FileUtility fileUtility;

    @Autowired
    private JwtManager jwtManager;

    @Autowired
    private Mapper<com.qiwenshare.file.office.entities.Permission, Permission> mapper;

    @Autowired
    private DefaultDocumentConfigurer defaultDocumentConfigurer;

    @Autowired
    private DefaultEditorConfigConfigurer defaultEditorConfigConfigurer;

    public void configure(FileModel fileModel, DefaultFileWrapper wrapper){  // define the file configurer
        if (fileModel != null){  // check if the file model is specified
            String fileName = wrapper.getFileName();  // get the fileName parameter from the file wrapper
            Action action = wrapper.getAction();  // get the action parameter from the file wrapper

            DocumentType documentType = fileUtility.getDocumentType(fileName);  // get the document type of the specified file
            fileModel.setDocumentType(documentType);  // set the document type to the file model
            fileModel.setType(wrapper.getType());  // set the platform type to the file model

//            Permission userPermissions = mapper.toModel(wrapper.getUser().getPermissions());  // convert the permission entity to the model
            Permission userPermissions = new Permission(); // TODO

                    String fileExt = fileUtility.getFileExtension(wrapper.getFileName());
            Boolean canEdit = fileUtility.getEditedExts().contains(fileExt);
            if ((!canEdit && action.equals(Action.edit) || action.equals(Action.fillForms)) && fileUtility.getFillExts().contains(fileExt)) {
                canEdit = true;
                wrapper.setAction(Action.fillForms);
            }
            wrapper.setCanEdit(canEdit);

            DefaultDocumentWrapper documentWrapper = DefaultDocumentWrapper  // define the document wrapper
                    .builder()
                    .fileName(fileName)
                    .permission(updatePermissions(userPermissions, action, canEdit))
                    .favorite(wrapper.getUser().getFavorite())
                    .previewUrl(wrapper.getActionData())
                    .build();

            defaultDocumentConfigurer.configure(fileModel.getDocument(),  documentWrapper);  // define the document configurer
            defaultEditorConfigConfigurer.configure(fileModel.getEditorConfig(), wrapper);  // define the editorConfig configurer

            Map<String, Object> map = new HashMap<>();
            map.put("type", fileModel.getType());
            map.put("documentType", documentType);
            map.put("document", fileModel.getDocument());
            map.put("editorConfig", fileModel.getEditorConfig());

            fileModel.setToken(jwtManager.createToken(map));  // create a token and set it to the file model
            JWT res = jwtManager.readToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlZGl0b3JDb25maWciOnsiY2FsbGJhY2tVcmwiOiJodHRwOi8vbG9jYWxob3N0OjQwMDAvdHJhY2s_ZmlsZU5hbWU9bmV3LmRvY3gmdXNlckFkZHJlc3M9RSUzQSU1Q2NvZGUlNUMlRTYlOEYlOTIlRTQlQkIlQjYyJTVDSmF2YStTcHJpbmcrRXhhbXBsZSU1Q0phdmErU3ByaW5nK0V4YW1wbGUlNUNkb2N1bWVudHMlNUMxOTIuMTY4LjEuNiU1QyIsImNyZWF0ZVVybCI6Imh0dHA6Ly9sb2NhbGhvc3Q6NDAwMC9jcmVhdGU_ZmlsZUV4dD1kb2N4JnNhbXBsZT1mYWxzZSIsImN1c3RvbWl6YXRpb24iOnsibG9nbyI6eyJpbWFnZSI6IiIsImltYWdlRW1iZWRkZWQiOiIiLCJ1cmwiOiJodHRwczovL3d3dy5vbmx5b2ZmaWNlLmNvbSJ9LCJnb2JhY2siOnsidXJsIjoiaHR0cDovL2xvY2FsaG9zdDo0MDAwLyJ9LCJhdXRvc2F2ZSI6dHJ1ZSwiY29tbWVudHMiOnRydWUsImNvbXBhY3RIZWFkZXIiOmZhbHNlLCJjb21wYWN0VG9vbGJhciI6ZmFsc2UsImNvbXBhdGlibGVGZWF0dXJlcyI6ZmFsc2UsImZvcmNlc2F2ZSI6ZmFsc2UsImhlbHAiOnRydWUsImhpZGVSaWdodE1lbnUiOmZhbHNlLCJoaWRlUnVsZXJzIjpmYWxzZSwic3VibWl0Rm9ybSI6ZmFsc2UsImFib3V0Ijp0cnVlLCJmZWVkYmFjayI6dHJ1ZX0sImVtYmVkZGVkIjp7fSwibGFuZyI6ImVuIiwibW9kZSI6ImVkaXQiLCJ1c2VyIjp7ImlkIjoiMSIsIm5hbWUiOiJKb2huIFNtaXRoIiwiZ3JvdXAiOiIifSwidGVtcGxhdGVzIjpbeyJpbWFnZSI6IiIsInRpdGxlIjoiQmxhbmsiLCJ1cmwiOiJodHRwOi8vbG9jYWxob3N0OjQwMDAvY3JlYXRlP2ZpbGVFeHQ9ZG9jeCZzYW1wbGU9ZmFsc2UifSx7ImltYWdlIjoiaHR0cDovL2xvY2FsaG9zdDo0MDAwL2Nzcy9pbWcvZmlsZV9kb2N4LnN2ZyIsInRpdGxlIjoiV2l0aCBzYW1wbGUgY29udGVudCIsInVybCI6Imh0dHA6Ly9sb2NhbGhvc3Q6NDAwMC9jcmVhdGU_ZmlsZUV4dD1kb2N4JnNhbXBsZT10cnVlIn1dfSwiZG9jdW1lbnRUeXBlIjoid29yZCIsImRvY3VtZW50Ijp7ImluZm8iOnsib3duZXIiOiJNZSIsInVwbG9hZGVkIjoiU3VuIE5vdiAyNyAyMDIyIn0sInBlcm1pc3Npb25zIjp7ImNvbW1lbnQiOnRydWUsImNvcHkiOnRydWUsImRvd25sb2FkIjp0cnVlLCJlZGl0Ijp0cnVlLCJwcmludCI6dHJ1ZSwiZmlsbEZvcm1zIjp0cnVlLCJtb2RpZnlGaWx0ZXIiOnRydWUsIm1vZGlmeUNvbnRlbnRDb250cm9sIjp0cnVlLCJyZXZpZXciOnRydWUsImNoYXQiOnRydWUsImNvbW1lbnRHcm91cHMiOnt9fSwiZmlsZVR5cGUiOiJkb2N4Iiwia2V5IjoiLTgxNjAzMzMxMiIsInVybFVzZXIiOiJodHRwOi8vbG9jYWxob3N0OjQwMDAvZG93bmxvYWQ_ZmlsZU5hbWU9bmV3LmRvY3gmdXNlckFkZHJlc3NFJTNBJTVDY29kZSU1QyVFNiU4RiU5MiVFNCVCQiVCNjIlNUNKYXZhK1NwcmluZytFeGFtcGxlJTVDSmF2YStTcHJpbmcrRXhhbXBsZSU1Q2RvY3VtZW50cyU1QzE5Mi4xNjguMS42JTVDIiwidGl0bGUiOiJuZXcuZG9jeCIsInVybCI6Imh0dHA6Ly9sb2NhbGhvc3Q6NDAwMC9kb3dubG9hZD9maWxlTmFtZT1uZXcuZG9jeCZ1c2VyQWRkcmVzcz1FJTNBJTVDY29kZSU1QyVFNiU4RiU5MiVFNCVCQiVCNjIlNUNKYXZhK1NwcmluZytFeGFtcGxlJTVDSmF2YStTcHJpbmcrRXhhbXBsZSU1Q2RvY3VtZW50cyU1QzE5Mi4xNjguMS42JTVDIiwiZGlyZWN0VXJsIjoiaHR0cDovL2xvY2FsaG9zdDo0MDAwL2Rvd25sb2FkP2ZpbGVOYW1lPW5ldy5kb2N4In0sInR5cGUiOiJkZXNrdG9wIn0.xZHMRgDUK76I_a_DOZSGJ1Fcxp_ghOCwR1FTqxwbnxE");
            System.out.println(JSON.toJSONString(res));
            JWT res2 = jwtManager.readToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlZGl0b3JDb25maWciOnsiY2FsbGJhY2tVcmwiOiJodHRwOi8vMTIxLjg5LjIyMi4xMDM6ODc2My9vZmZpY2UvSW5kZXhTZXJ2bGV0P3R5cGU9ZWRpdCZ1c2VyRmlsZUlkPTE1ODUxMzQyOTcyMjQwNDQ1NDQmdG9rZW49QmVhcmVyIGV5SmhiR2NpT2lKSVV6STFOaUo5LmV5SnBjM01pT2lKeGFYZGxiaTFqYlhNaUxDSmxlSEFpT2pFMk5qazNPVFV3T0RJc0luTjFZaUk2SW50Y0luVnpaWEpKWkZ3aU9qSjlJaXdpWVhWa0lqb2ljV2wzWlc1emFHRnlaU0lzSW1saGRDSTZNVFkyT1RFNU1ESTRNbjAuYzJwa1hwejFjMHZRcU9zX3I2V0JySnJDZ1lnbUtNMGc2Q2YxZEhVaG51cyIsImNyZWF0ZVVybCI6Imh0dHA6Ly8xMjEuODkuMjIyLjEwMzo4NzYzL2NyZWF0ZT9maWxlRXh0PXhsc3gmc2FtcGxlPWZhbHNlIiwiY3VzdG9taXphdGlvbiI6eyJsb2dvIjp7ImltYWdlIjoiIiwiaW1hZ2VFbWJlZGRlZCI6IiIsInVybCI6Imh0dHBzOi8vd3d3Lm9ubHlvZmZpY2UuY29tIn0sImdvYmFjayI6eyJ1cmwiOiJodHRwOi8vMTIxLjg5LjIyMi4xMDM6ODc2My8ifSwiYXV0b3NhdmUiOnRydWUsImNvbW1lbnRzIjp0cnVlLCJjb21wYWN0SGVhZGVyIjpmYWxzZSwiY29tcGFjdFRvb2xiYXIiOmZhbHNlLCJjb21wYXRpYmxlRmVhdHVyZXMiOmZhbHNlLCJmb3JjZXNhdmUiOmZhbHNlLCJoZWxwIjp0cnVlLCJoaWRlUmlnaHRNZW51IjpmYWxzZSwiaGlkZVJ1bGVycyI6ZmFsc2UsInN1Ym1pdEZvcm0iOmZhbHNlLCJhYm91dCI6dHJ1ZSwiZmVlZGJhY2siOnRydWV9LCJlbWJlZGRlZCI6e30sImxhbmciOiJ6aCIsIm1vZGUiOiJ2aWV3IiwidXNlciI6eyJpZCI6IjEiLCJuYW1lIjoiSm9obiBTbWl0aCIsImdyb3VwIjoiIn0sInRlbXBsYXRlcyI6W3siaW1hZ2UiOiIiLCJ0aXRsZSI6IkJsYW5rIiwidXJsIjoiaHR0cDovLzEyMS44OS4yMjIuMTAzOjg3NjMvY3JlYXRlP2ZpbGVFeHQ9eGxzeCZzYW1wbGU9ZmFsc2UifSx7ImltYWdlIjoiaHR0cDovLzEyMS44OS4yMjIuMTAzOjg3NjMvY3NzL2ltZy9maWxlX3hsc3guc3ZnIiwidGl0bGUiOiJXaXRoIHNhbXBsZSBjb250ZW50IiwidXJsIjoiaHR0cDovLzEyMS44OS4yMjIuMTAzOjg3NjMvY3JlYXRlP2ZpbGVFeHQ9eGxzeCZzYW1wbGU9dHJ1ZSJ9XX0sImRvY3VtZW50VHlwZSI6ImNlbGwiLCJkb2N1bWVudCI6eyJpbmZvIjp7Im93bmVyIjoiTWUiLCJ1cGxvYWRlZCI6Ik1vbiBOb3YgMjggMjAyMiJ9LCJwZXJtaXNzaW9ucyI6eyJjb21tZW50IjpmYWxzZSwiY29weSI6dHJ1ZSwiZG93bmxvYWQiOnRydWUsImVkaXQiOnRydWUsInByaW50Ijp0cnVlLCJmaWxsRm9ybXMiOmZhbHNlLCJtb2RpZnlGaWx0ZXIiOnRydWUsIm1vZGlmeUNvbnRlbnRDb250cm9sIjp0cnVlLCJyZXZpZXciOmZhbHNlLCJjaGF0Ijp0cnVlLCJjb21tZW50R3JvdXBzIjp7ImFhYSI6ImJiYiJ9fSwiZmlsZVR5cGUiOiJ4bHN4Iiwia2V5IjoiLTExMzc3NjkyMDciLCJ1cmxVc2VyIjoiaHR0cDovLzEyMS44OS4yMjIuMTAzOjg3NjMvZG93bmxvYWQ_ZmlsZU5hbWU9JUU5JTk3JUFFJUU5JUEyJTk4JUU4JUFFJUIwJUU1JUJEJTk1Lnhsc3gmdXNlckFkZHJlc3MlMkZyb290JTJGZ2l0ZWVfZ28lMkZkZXBsb3klMkZxaXdlbi1maWxlJTJGZG9jdW1lbnRzJTJGMTcyLjI2LjIxMS4yMDklMkYiLCJ0aXRsZSI6IumXrumimOiusOW9lS54bHN4IiwidXJsIjoiaHR0cHM6Ly90ZXN0cGFuLnFpd2Vuc2hhcmUuY29tL2FwaS9maWxldHJhbnNmZXIvcHJldmlldz91c2VyRmlsZUlkPTE1ODUxMzQyOTcyMjQwNDQ1NDQmaXNNaW49ZmFsc2Umc2hhcmVCYXRjaE51bT11bmRlZmluZWQmZXh0cmFjdGlvbkNvZGU9dW5kZWZpbmVkJnRva2VuPUJlYXJlciBleUpoYkdjaU9pSklVekkxTmlKOS5leUpwYzNNaU9pSnhhWGRsYmkxamJYTWlMQ0psZUhBaU9qRTJOamszT1RVd09ESXNJbk4xWWlJNkludGNJblZ6WlhKSlpGd2lPako5SWl3aVlYVmtJam9pY1dsM1pXNXphR0Z5WlNJc0ltbGhkQ0k2TVRZMk9URTVNREk0TW4wLmMycGtYcHoxYzB2UXFPc19yNldCckpyQ2dZZ21LTTBnNkNmMWRIVWhudXMiLCJkaXJlY3RVcmwiOiJodHRwOi8vMTIxLjg5LjIyMi4xMDM6ODc2My9kb3dubG9hZD9maWxlTmFtZT0lRTklOTclQUUlRTklQTIlOTglRTglQUUlQjAlRTUlQkQlOTUueGxzeCJ9LCJ0eXBlIjoiZGVza3RvcCJ9.O5eBtPY0eA9CCCkbglO2gt4L42CnBtJsvoOUnL3PujM");
            System.out.println(JSON.toJSONString(res2));
        }
    }



    @Override
    public FileModel getFileModel(DefaultFileWrapper wrapper) {  // get file model
        FileModel fileModel = fileModelObjectFactory.getObject();
        configure(fileModel, wrapper);  // and configure it
        return fileModel;
    }

    private Permission updatePermissions(Permission userPermissions, Action action, Boolean canEdit) {
        userPermissions.setComment(
                !action.equals(Action.view)
                        && !action.equals(Action.fillForms)
                        && !action.equals(Action.embedded)
                        && !action.equals(Action.blockcontent)
        );

        userPermissions.setFillForms(
                !action.equals(Action.view)
                        && !action.equals(Action.comment)
                        && !action.equals(Action.embedded)
                        && !action.equals(Action.blockcontent)
        );

        userPermissions.setReview(canEdit &&
                (action.equals(Action.review) || action.equals(Action.edit)));

        userPermissions.setEdit(canEdit &&
                (action.equals(Action.view)
                || action.equals(Action.edit)
                || action.equals(Action.filter)
                || action.equals(Action.blockcontent)));

        return userPermissions;
    }
}
