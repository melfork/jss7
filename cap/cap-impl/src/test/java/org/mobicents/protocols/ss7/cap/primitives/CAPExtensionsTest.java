/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.mobicents.protocols.ss7.cap.primitives;

import static org.testng.Assert.*;

import java.io.IOException;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.ss7.cap.api.primitives.CAPExtensions;
import org.mobicents.protocols.ss7.cap.api.primitives.CriticalityType;
import org.mobicents.protocols.ss7.cap.api.primitives.ExtensionField;
import org.testng.*;import org.testng.annotations.*;

/**
 * 
 * @author sergey vetyutnev
 * 
 */
public class CAPExtensionsTest {

	public byte[] getData1() {
		return new byte[] { 48, 18, 48, 5, 2, 1, 2, (byte) 129, 0, 48, 9, 2, 1, 3, 10, 1, 1, (byte) 129, 1, (byte) 255 };
	}

	@Test(groups = { "functional.decode","primitives"})
	public void testDecode() throws Exception {

		byte[] data = this.getData1();
		AsnInputStream ais = new AsnInputStream(data);
		CAPExtensionsImpl elem = new CAPExtensionsImpl();
		int tag = ais.readTag();
		elem.decodeAll(ais);
		assertTrue(checkTestCAPExtensions(elem));
	}

	@Test(groups = { "functional.encode","primitives"})
	public void testEncode() throws Exception {

		CAPExtensionsImpl elem = createTestCAPExtensions();
		AsnOutputStream aos = new AsnOutputStream();
		elem.encodeAll(aos);
		assertTrue(Arrays.equals(aos.toByteArray(), this.getData1()));
	}
	
	public static CAPExtensionsImpl createTestCAPExtensions(){
		AsnOutputStream aos = new AsnOutputStream();
		aos.writeNullData();
		ExtensionFieldImpl a1 = new ExtensionFieldImpl(2, CriticalityType.typeIgnore, aos.toByteArray());
		aos = new AsnOutputStream();
		try {
			aos.writeBooleanData(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ExtensionFieldImpl a2 = new ExtensionFieldImpl(3, CriticalityType.typeAbort, aos.toByteArray());
		CAPExtensionsImpl elem = new CAPExtensionsImpl(new ExtensionField[] { a1, a2 });
		return elem;
	}

	public static boolean checkTestCAPExtensions(CAPExtensions elem) {
		if (elem.getExtensionFields() == null || elem.getExtensionFields().length != 2)
			return false;
		
		ExtensionField a1 = elem.getExtensionFields()[0];
		ExtensionField a2 = elem.getExtensionFields()[1];
		if (a1.getLocalCode() != 2 || a2.getLocalCode() != 3)
			return false;
		if (a1.getCriticalityType() != CriticalityType.typeIgnore || a2.getCriticalityType() != CriticalityType.typeAbort)
			return false;
		if (a1.getData() == null || a1.getData().length != 0)
			return false;
		if (a2.getData() == null || a2.getData().length != 1 || (a2.getData()[0]) != -1)
			return false;
		
		return true;
	}
}